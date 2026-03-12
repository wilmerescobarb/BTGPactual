package com.ceiba.bgt_api_investment.service;

import com.ceiba.bgt_api_investment.dto.CustomerInvestmentDto;
import com.ceiba.bgt_api_investment.dto.InvestmentSummaryDto;
import com.ceiba.bgt_api_investment.exception.BusinessException;
import com.ceiba.bgt_api_investment.model.Customer;
import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import com.ceiba.bgt_api_investment.repository.CustomerInvestmentRepository;
import com.ceiba.bgt_api_investment.repository.CustomerRepository;
import com.ceiba.bgt_api_investment.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servicio que gestiona la suscripción de un cliente a un fondo de inversión.
 */
@Service
@RequiredArgsConstructor
public class CustomerInvestmentService {

    private final CustomerRepository customerRepository;
    private final InvestmentRepository investmentRepository;
    private final CustomerInvestmentRepository customerInvestmentRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    /**
     * Actualiza solo el campo "amount" del customer sin reemplazar el documento completo,
     * evitando que el JSON Schema validator rechace el update por campos faltantes.
     */
    private Mono<Void> updateCustomerAmount(String customerId, BigDecimal newAmount) {
        Query query = new Query(Criteria.where("_id").is(customerId));
        Update update = new Update().set("amount", newAmount);
        return mongoTemplate.updateFirst(query, update, Customer.class).then();
    }

    /**
     * Obtiene las inversiones del cliente buscando sus CustomerInvestments
     * y enriqueciendo con el nombre del fondo (sin JOIN, approach MongoDB).
     */
    public Flux<InvestmentSummaryDto> getInvestments(String username) {
        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Cliente con el usuario: " + username + " no existe")))
                .flatMapMany(customer ->
                        customerInvestmentRepository.findByIdCustomer(customer.getId())
                                .flatMap(ci ->
                                        investmentRepository.findById(ci.getIdInvestment().toString())
                                                .map(investment -> InvestmentSummaryDto.from(ci, investment))
                                )
                );
    }

    public Mono<CustomerInvestmentDto> unsubscribe(String username, String idCustomerInvestment) {

        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Cliente con el usuario: " + username + " no existe")))
                .flatMap(customer ->
                        customerInvestmentRepository.findById(idCustomerInvestment)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                        "No existe suscripción con el id: " + idCustomerInvestment)))
                                .flatMap(ci -> {

                                    // Regla 1: la inversión debe pertenecer al customer autenticado
                                    if (!ci.getIdCustomer().equals(customer.getId())) {
                                        return Mono.error(new BusinessException(
                                                "La suscripción no pertenece al cliente autenticado"));
                                    }

                                    // Regla 2: no debe estar ya cancelada
                                    if ("C".equals(ci.getStatus())) {
                                        return Mono.error(new BusinessException(
                                                "La suscripción ya se encuentra cancelada"));
                                    }

                                    // Devolver el invested_amount al saldo del customer
                                    LocalDateTime closedAt = LocalDateTime.now();
                                    BigDecimal newBalance = customer.getAmount().add(ci.getInvestedAmount());
                                    ci.setStatus("C");
                                    ci.setClosedAt(closedAt);

                                    return updateCustomerAmount(customer.getId().toString(), newBalance)
                                            .then(customerInvestmentRepository.save(ci))
                                            .map(saved -> CustomerInvestmentDto.from(saved));
                                })
                );
    }

    public Mono<CustomerInvestmentDto> subscribe(String username, CustomerInvestmentDto request) {

        LocalDateTime openedAt = LocalDateTime.now();

        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Cliente con el usuario: " + username + " no existe")))
                .flatMap(customer ->
                        investmentRepository.findById(request.getInvestment())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                        "No existe fondo de inversión con el id: " + request.getInvestment())))
                                .flatMap(investment -> {

                                    // Regla 1: amount >= min_amount del fondo
                                    if (request.getAmount().compareTo(investment.getMinAmount()) < 0) {
                                        return Mono.error(new BusinessException(
                                                "El monto es inferior al mínimo permitido para la inversión"));
                                    }

                                    // Regla 2: el customer debe tener saldo suficiente
                                    BigDecimal newBalance = customer.getAmount().subtract(request.getAmount());
                                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                                        return Mono.error(new BusinessException(
                                                "No tiene saldo disponible para vincularse al fondo "
                                                        + investment.getName()));
                                    }

                                    customer.setAmount(newBalance);
                                    CustomerInvestment entity = CustomerInvestment.builder()
                                            .idCustomer(customer.getId())
                                            .idInvestment(investment.getId())
                                            .openedAt(openedAt)
                                            .investedAmount(request.getAmount())
                                            .status("A")
                                            .build();
                                    return updateCustomerAmount(customer.getId().toString(), newBalance)
                                            .then(customerInvestmentRepository.save(entity))
                                            .map(CustomerInvestmentDto::from);
                                })
                );
    }
}
