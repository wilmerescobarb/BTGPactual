package com.ceiba.bgt_api_investment.service;

import com.ceiba.bgt_api_investment.dto.CustomerInvestmentRequest;
import com.ceiba.bgt_api_investment.dto.CustomerInvestmentResponse;
import com.ceiba.bgt_api_investment.dto.InvestmentSummaryDto;
import com.ceiba.bgt_api_investment.exception.BusinessException;
import com.ceiba.bgt_api_investment.exception.ErrorMessages;
import com.ceiba.bgt_api_investment.model.Customer;
import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import com.ceiba.bgt_api_investment.model.CustomerInvestmentStatus;
import com.ceiba.bgt_api_investment.repository.CustomerInvestmentRepository;
import com.ceiba.bgt_api_investment.repository.CustomerRepository;
import com.ceiba.bgt_api_investment.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que gestiona la suscripción de un cliente a un fondo de inversión.
 */
@Service
@RequiredArgsConstructor
public class CustomerInvestmentService {

    private static final String FIELD_ID = "_id";
    private static final String FIELD_AMOUNT = "amount";

    private final CustomerRepository customerRepository;
    private final InvestmentRepository investmentRepository;
    private final CustomerInvestmentRepository customerInvestmentRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    /**
     * Actualiza solo el campo "amount" del customer sin reemplazar el documento completo,
     * evitando que el JSON Schema validator rechace el update por campos faltantes.
     */
    private Mono<Void> updateCustomerAmount(String customerId, BigDecimal newAmount) {
        Query query = new Query(Criteria.where(FIELD_ID).is(customerId));
        Update update = new Update().set(FIELD_AMOUNT, newAmount);
        return mongoTemplate.updateFirst(query, update, Customer.class).then();
    }

    /**
     * Obtiene las inversiones del cliente buscando sus CustomerInvestments
     * y enriqueciendo con el nombre del fondo (sin JOIN, approach MongoDB).
     */
    public Flux<InvestmentSummaryDto> getInvestments(String username) {
        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        String.format(ErrorMessages.CUSTOMER_NOT_FOUND, username))))
                .flatMapMany(customer ->
                        customerInvestmentRepository.findByIdCustomer(customer.getId())
                                .collectList()
                                .flatMapMany(ciList -> {
                                    List<String> investmentIds = ciList.stream()
                                            .map(ci -> ci.getIdInvestment().toString())
                                            .toList();

                                    return investmentRepository.findAllById(investmentIds)
                                            .collectMap(inv -> inv.getId().toHexString())
                                            .flatMapMany(investmentMap ->
                                                    Flux.fromIterable(ciList)
                                                            .flatMap(ci -> {
                                                                var investment = investmentMap
                                                                        .get(ci.getIdInvestment().toHexString());
                                                                if (investment == null) {
                                                                    return Mono.error(new IllegalStateException(
                                                                            String.format(ErrorMessages.INVESTMENT_DATA_INCONSISTENT, ci.getIdInvestment())));
                                                                }
                                                                return Mono.just(InvestmentSummaryDto.from(ci, investment));
                                                            })
                                            );
                                })
                );
    }

    public Mono<CustomerInvestmentResponse> unsubscribe(String username, String idCustomerInvestment) {

        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        String.format(ErrorMessages.CUSTOMER_NOT_FOUND, username))))
                .flatMap(customer ->
                        customerInvestmentRepository.findById(idCustomerInvestment)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                        String.format(ErrorMessages.SUBSCRIPTION_NOT_FOUND, idCustomerInvestment))))
                                .flatMap(ci -> {

                                    // Regla 1: la inversión debe pertenecer al customer autenticado
                                    if (!ci.getIdCustomer().equals(customer.getId())) {
                                        return Mono.error(new BusinessException(
                                                ErrorMessages.SUBSCRIPTION_NOT_OWNER));
                                    }

                                    // Regla 2: no debe estar ya cancelada
                                    if (CustomerInvestmentStatus.CANCELLED.equals(ci.getStatus())) {
                                        return Mono.error(new BusinessException(
                                                ErrorMessages.SUBSCRIPTION_ALREADY_CANCELLED));
                                    }

                                    // Devolver el invested_amount al saldo del customer
                                    LocalDateTime closedAt = LocalDateTime.now();
                                    BigDecimal newBalance = customer.getAmount().add(ci.getInvestedAmount());
                                    ci.setStatus(CustomerInvestmentStatus.CANCELLED);
                                    ci.setClosedAt(closedAt);

                                    return updateCustomerAmount(customer.getId().toString(), newBalance)
                                            .then(customerInvestmentRepository.save(ci))
                                            .map(CustomerInvestmentResponse::from);
                                })
                );
    }

    public Mono<CustomerInvestmentResponse> subscribe(String username, CustomerInvestmentRequest request) {

        LocalDateTime openedAt = LocalDateTime.now();

        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        String.format(ErrorMessages.CUSTOMER_NOT_FOUND, username))))
                .flatMap(customer ->
                        investmentRepository.findById(request.getInvestment())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                        String.format(ErrorMessages.INVESTMENT_NOT_FOUND, request.getInvestment()))))
                                .flatMap(investment -> {

                                    // Regla 1: amount >= min_amount del fondo
                                    if (request.getAmount().compareTo(investment.getMinAmount()) < 0) {
                                        return Mono.error(new BusinessException(
                                                ErrorMessages.AMOUNT_BELOW_MINIMUM));
                                    }

                                    // Regla 2: el customer debe tener saldo suficiente
                                    BigDecimal newBalance = customer.getAmount().subtract(request.getAmount());
                                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                                        return Mono.error(new BusinessException(
                                                String.format(ErrorMessages.INSUFFICIENT_BALANCE, investment.getName())));
                                    }

                                    CustomerInvestment entity = CustomerInvestment.builder()
                                            .idCustomer(customer.getId())
                                            .idInvestment(investment.getId())
                                            .openedAt(openedAt)
                                            .investedAmount(request.getAmount())
                                            .status(CustomerInvestmentStatus.ACTIVE)
                                            .build();
                                    return updateCustomerAmount(customer.getId().toString(), newBalance)
                                            .then(customerInvestmentRepository.save(entity))
                                            .map(CustomerInvestmentResponse::from);
                                })
                );
    }
}
