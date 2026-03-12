package com.ceiba.bgt_api_investment.service;

import com.ceiba.bgt_api_investment.dto.CustomerInvestmentRequest;
import com.ceiba.bgt_api_investment.exception.BusinessException;
import com.ceiba.bgt_api_investment.exception.ErrorMessages;
import com.ceiba.bgt_api_investment.model.Customer;
import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import com.ceiba.bgt_api_investment.model.CustomerInvestmentStatus;
import com.ceiba.bgt_api_investment.model.Investment;
import com.ceiba.bgt_api_investment.repository.CustomerInvestmentRepository;
import com.ceiba.bgt_api_investment.repository.CustomerRepository;
import com.ceiba.bgt_api_investment.repository.InvestmentRepository;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link CustomerInvestmentService}.
 * Se usan mocks para todos los repositorios y clientes externos,
 * y StepVerifier para validar el comportamiento reactivo.
 */
@ExtendWith(MockitoExtension.class)
class CustomerInvestmentServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private InvestmentRepository investmentRepository;
    @Mock private CustomerInvestmentRepository customerInvestmentRepository;
    @Mock private ReactiveMongoTemplate mongoTemplate;
    @Mock private NotificationWebClient notificationWebClient;

    @InjectMocks
    private CustomerInvestmentService service;

    // ── datos de prueba compartidos ────────────────────────────────────────

    private ObjectId customerId;
    private ObjectId investmentId;
    private Customer customer;
    private Investment investment;

    @BeforeEach
    void setUp() {
        customerId   = new ObjectId();
        investmentId = new ObjectId();

        customer = new Customer();
        customer.setId(customerId);
        customer.setUsername("testuser");
        customer.setAmount(new BigDecimal("1000.00"));
        customer.setEmail("test@example.com");
        customer.setCellphone("3001234567");

        investment = new Investment();
        investment.setId(investmentId);
        investment.setName("FIC Renta Fija");
        investment.setMinAmount(new BigDecimal("100.00"));
        investment.setCategory("RENTA_FIJA");
    }

    // ── mock helper ────────────────────────────────────────────────────────

    /** Configura el mongoTemplate mock para simular una actualización exitosa. */
    private void mockMongoUpdate() {
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Customer.class)))
                .thenReturn(Mono.just(mock(UpdateResult.class)));
    }

    // ══════════════════════════════════════════════════════════════════════
    // getCatalog
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getCatalog")
    class GetCatalog {

        @Test
        @DisplayName("Retorna todos los fondos mapeados a InvestmentDto")
        void returnsAllInvestmentsMappedToDto() {
            when(investmentRepository.findAll()).thenReturn(Flux.just(investment));

            StepVerifier.create(service.getCatalog())
                    .assertNext(dto -> {
                        assertThat(dto.getId()).isEqualTo(investmentId.toHexString());
                        assertThat(dto.getName()).isEqualTo("FIC Renta Fija");
                        assertThat(dto.getMinAmount()).isEqualByComparingTo("100.00");
                        assertThat(dto.getCategory()).isEqualTo("RENTA_FIJA");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Retorna Flux vacío cuando no hay fondos")
        void returnsEmptyWhenNoInvestments() {
            when(investmentRepository.findAll()).thenReturn(Flux.empty());

            StepVerifier.create(service.getCatalog())
                    .verifyComplete();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // getInvestments
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getInvestments")
    class GetInvestments {

        @Test
        @DisplayName("Retorna resúmenes de inversión enriquecidos con nombre del fondo")
        void returnsEnrichedSummaries() {
            CustomerInvestment ci = CustomerInvestment.builder()
                    .id("ci-001")
                    .idCustomer(customerId)
                    .idInvestment(investmentId)
                    .openedAt(LocalDateTime.now())
                    .investedAmount(new BigDecimal("500.00"))
                    .status(CustomerInvestmentStatus.ACTIVE)
                    .build();

            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(customerInvestmentRepository.findByIdCustomer(customerId)).thenReturn(Flux.just(ci));
            when(investmentRepository.findAllById(any(Iterable.class))).thenReturn(Flux.just(investment));

            StepVerifier.create(service.getInvestments("testuser"))
                    .assertNext(dto -> {
                        assertThat(dto.getIdCustomerInvestment()).isEqualTo("ci-001");
                        assertThat(dto.getIdInvestment()).isEqualTo(investmentId.toHexString());
                        assertThat(dto.getNameInvestment()).isEqualTo("FIC Renta Fija");
                        assertThat(dto.getInvestmentAmount()).isEqualByComparingTo("500.00");
                        assertThat(dto.getStatus()).isEqualTo(CustomerInvestmentStatus.ACTIVE);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Retorna Flux vacío cuando el cliente no tiene suscripciones")
        void returnsEmptyWhenNoSubscriptions() {
            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(customerInvestmentRepository.findByIdCustomer(customerId)).thenReturn(Flux.empty());
            when(investmentRepository.findAllById(any(Iterable.class))).thenReturn(Flux.empty());

            StepVerifier.create(service.getInvestments("testuser"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Error cuando el cliente no existe")
        void throwsWhenCustomerNotFound() {
            when(customerRepository.findByUsername("unknown")).thenReturn(Mono.empty());

            StepVerifier.create(service.getInvestments("unknown"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(IllegalArgumentException.class);
                        assertThat(err.getMessage()).contains("unknown");
                    })
                    .verify();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // subscribe
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("subscribe")
    class Subscribe {

        private CustomerInvestmentRequest buildRequest(BigDecimal amount) {
            CustomerInvestmentRequest req = new CustomerInvestmentRequest();
            req.setInvestment(investmentId.toHexString());
            req.setAmount(amount);
            req.setNotificationSms(false);
            req.setNotificationEmail(false);
            return req;
        }

        @Test
        @DisplayName("Crea suscripción y descuenta el saldo del cliente")
        void createsSubscriptionSuccessfully() {
            CustomerInvestmentRequest request = buildRequest(new BigDecimal("500.00"));

            CustomerInvestment savedEntity = CustomerInvestment.builder()
                    .id("new-ci")
                    .idCustomer(customerId)
                    .idInvestment(investmentId)
                    .openedAt(LocalDateTime.now())
                    .investedAmount(new BigDecimal("500.00"))
                    .status(CustomerInvestmentStatus.ACTIVE)
                    .build();

            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(investmentRepository.findById(investmentId.toHexString())).thenReturn(Mono.just(investment));
            mockMongoUpdate();
            when(customerInvestmentRepository.save(any(CustomerInvestment.class))).thenReturn(Mono.just(savedEntity));
            when(notificationWebClient.sendNotifications(any(), any(), any(), any())).thenReturn(Mono.empty());

            StepVerifier.create(service.subscribe("testuser", request, "Bearer token"))
                    .assertNext(resp -> {
                        assertThat(resp.getInvestment()).isEqualTo(investmentId.toHexString());
                        assertThat(resp.getAmount()).isEqualByComparingTo("500.00");
                        assertThat(resp.getStatus()).isEqualTo(CustomerInvestmentStatus.ACTIVE);
                        assertThat(resp.getOpenedAt()).isNotNull();
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Error cuando el cliente no existe")
        void throwsWhenCustomerNotFound() {
            when(customerRepository.findByUsername("ghost")).thenReturn(Mono.empty());

            StepVerifier.create(service.subscribe("ghost", buildRequest(new BigDecimal("500.00")), "Bearer token"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(IllegalArgumentException.class);
                        assertThat(err.getMessage()).contains("ghost");
                    })
                    .verify();
        }

        @Test
        @DisplayName("Error cuando el fondo de inversión no existe")
        void throwsWhenInvestmentNotFound() {
            CustomerInvestmentRequest request = new CustomerInvestmentRequest();
            request.setInvestment("nonexistentId");
            request.setAmount(new BigDecimal("500.00"));

            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(investmentRepository.findById("nonexistentId")).thenReturn(Mono.empty());

            StepVerifier.create(service.subscribe("testuser", request, "Bearer token"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(IllegalArgumentException.class);
                        assertThat(err.getMessage()).contains("nonexistentId");
                    })
                    .verify();
        }

        @Test
        @DisplayName("Error BusinessException cuando el monto es menor al mínimo del fondo")
        void throwsWhenAmountBelowMinimum() {
            // minAmount = 100, amount = 50
            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(investmentRepository.findById(investmentId.toHexString())).thenReturn(Mono.just(investment));

            StepVerifier.create(service.subscribe("testuser", buildRequest(new BigDecimal("50.00")), "Bearer token"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(BusinessException.class);
                        assertThat(err.getMessage()).isEqualTo(ErrorMessages.AMOUNT_BELOW_MINIMUM);
                    })
                    .verify();
        }

        @Test
        @DisplayName("Error BusinessException cuando el cliente no tiene saldo suficiente")
        void throwsWhenInsufficientBalance() {
            // customer.amount = 1000, request.amount = 2000
            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(investmentRepository.findById(investmentId.toHexString())).thenReturn(Mono.just(investment));

            StepVerifier.create(service.subscribe("testuser", buildRequest(new BigDecimal("2000.00")), "Bearer token"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(BusinessException.class);
                        assertThat(err.getMessage()).contains(investment.getName());
                    })
                    .verify();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // unsubscribe
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("unsubscribe")
    class Unsubscribe {

        private CustomerInvestment buildActiveInvestment(ObjectId ownerId) {
            return CustomerInvestment.builder()
                    .id("ci-001")
                    .idCustomer(ownerId)
                    .idInvestment(investmentId)
                    .openedAt(LocalDateTime.now())
                    .investedAmount(new BigDecimal("500.00"))
                    .status(CustomerInvestmentStatus.ACTIVE)
                    .build();
        }

        @Test
        @DisplayName("Cancela la suscripción y devuelve el invested_amount al saldo del cliente")
        void cancelsSubscriptionSuccessfully() {
            CustomerInvestment activeCi  = buildActiveInvestment(customerId);
            CustomerInvestment cancelledCi = CustomerInvestment.builder()
                    .id("ci-001")
                    .idCustomer(customerId)
                    .idInvestment(investmentId)
                    .openedAt(activeCi.getOpenedAt())
                    .closedAt(LocalDateTime.now())
                    .investedAmount(new BigDecimal("500.00"))
                    .status(CustomerInvestmentStatus.CANCELLED)
                    .build();

            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(customerInvestmentRepository.findById("ci-001")).thenReturn(Mono.just(activeCi));
            mockMongoUpdate();
            when(customerInvestmentRepository.save(any(CustomerInvestment.class))).thenReturn(Mono.just(cancelledCi));

            StepVerifier.create(service.unsubscribe("testuser", "ci-001"))
                    .assertNext(resp -> {
                        assertThat(resp.getStatus()).isEqualTo(CustomerInvestmentStatus.CANCELLED);
                        assertThat(resp.getClosedAt()).isNotNull();
                        assertThat(resp.getAmount()).isEqualByComparingTo("500.00");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Error cuando el cliente no existe")
        void throwsWhenCustomerNotFound() {
            when(customerRepository.findByUsername("ghost")).thenReturn(Mono.empty());

            StepVerifier.create(service.unsubscribe("ghost", "ci-001"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(IllegalArgumentException.class);
                        assertThat(err.getMessage()).contains("ghost");
                    })
                    .verify();
        }

        @Test
        @DisplayName("Error cuando la suscripción no existe")
        void throwsWhenSubscriptionNotFound() {
            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(customerInvestmentRepository.findById("ci-999")).thenReturn(Mono.empty());

            StepVerifier.create(service.unsubscribe("testuser", "ci-999"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(IllegalArgumentException.class);
                        assertThat(err.getMessage()).contains("ci-999");
                    })
                    .verify();
        }

        @Test
        @DisplayName("Error BusinessException cuando la suscripción no pertenece al cliente autenticado")
        void throwsWhenSubscriptionNotOwnedByCustomer() {
            ObjectId otherCustomerId = new ObjectId();
            CustomerInvestment ci = buildActiveInvestment(otherCustomerId);

            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(customerInvestmentRepository.findById("ci-001")).thenReturn(Mono.just(ci));

            StepVerifier.create(service.unsubscribe("testuser", "ci-001"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(BusinessException.class);
                        assertThat(err.getMessage()).isEqualTo(ErrorMessages.SUBSCRIPTION_NOT_OWNER);
                    })
                    .verify();
        }

        @Test
        @DisplayName("Error BusinessException cuando la suscripción ya está cancelada")
        void throwsWhenAlreadyCancelled() {
            CustomerInvestment cancelledCi = CustomerInvestment.builder()
                    .id("ci-001")
                    .idCustomer(customerId)
                    .idInvestment(investmentId)
                    .investedAmount(new BigDecimal("500.00"))
                    .status(CustomerInvestmentStatus.CANCELLED)
                    .build();

            when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
            when(customerInvestmentRepository.findById("ci-001")).thenReturn(Mono.just(cancelledCi));

            StepVerifier.create(service.unsubscribe("testuser", "ci-001"))
                    .expectErrorSatisfies(err -> {
                        assertThat(err).isInstanceOf(BusinessException.class);
                        assertThat(err.getMessage()).isEqualTo(ErrorMessages.SUBSCRIPTION_ALREADY_CANCELLED);
                    })
                    .verify();
        }
    }
}
