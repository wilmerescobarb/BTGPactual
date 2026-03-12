package com.ceiba.bgt_api_customer.service;

import com.ceiba.bgt_api_customer.constant.CustomerConstants;
import com.ceiba.bgt_api_customer.dto.CustomerDto;
import com.ceiba.bgt_api_customer.dto.RegisterRequest;
import com.ceiba.bgt_api_customer.exception.BusinessException;
import com.ceiba.bgt_api_customer.exception.ErrorMessages;
import com.ceiba.bgt_api_customer.model.Customer;
import com.ceiba.bgt_api_customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Servicio con la lógica de negocio para la gestión de clientes.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retorna la información pública del cliente autenticado.
     *
     * @param username nombre de usuario extraído del JWT
     * @return DTO con id, username, amount, names y lastnames
     */
    public Mono<CustomerDto> getCustomer(String username) {
        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        String.format(ErrorMessages.CUSTOMER_NOT_FOUND, username))))
                .map(this::toDto);
    }

    /**
     * Registra un nuevo cliente en la base de datos.
     * La contraseña se hashea con BCrypt antes de persistirla.
     *
     * @param request datos del nuevo cliente
     * @return DTO con la información pública del cliente creado
     */
    public Mono<CustomerDto> register(RegisterRequest request) {
        validateRequest(request);

        Customer customer = Customer.builder()
                .names(request.getNames())
                .lastnames(request.getLastnames())
                .birthday(request.getBirthday())
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .cellphone(request.getCellphone())
                .email(request.getEmail())
                .username(request.getUsername())
                .passUser(passwordEncoder.encode(request.getPassUser()))
                .amount(CustomerConstants.INITIAL_AMOUNT)
                .createdAt(LocalDate.now())
                .build();

        return customerRepository.save(customer)
                .map(this::toDto);
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private void validateRequest(RegisterRequest request) {
        validateField(request.getNames(),          "names");
        validateField(request.getLastnames(),      "lastnames");
        validateField(request.getDocumentType(),   "documentType");
        validateField(request.getDocumentNumber(), "documentNumber");
        validateField(request.getCellphone(),      "cellphone");
        validateField(request.getEmail(),          "email");
        validateField(request.getUsername(),       "username");
        validateField(request.getPassUser(),       "passUser");
        if (request.getBirthday() == null) {
            throw new BusinessException(String.format(ErrorMessages.FIELD_REQUIRED, "birthday"));
        }
    }

    private void validateField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(String.format(ErrorMessages.FIELD_REQUIRED, fieldName));
        }
    }

    private CustomerDto toDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getUsername(),
                customer.getAmount(),
                customer.getNames(),
                customer.getLastnames()
        );
    }
}
