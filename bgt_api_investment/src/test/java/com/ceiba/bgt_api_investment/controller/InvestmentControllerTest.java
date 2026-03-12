package com.ceiba.bgt_api_investment.controller;

import com.ceiba.bgt_api_investment.config.WebFluxTestSecurityConfig;
import com.ceiba.bgt_api_investment.constant.ResponseMessages;
import com.ceiba.bgt_api_investment.dto.CustomerInvestmentRequest;
import com.ceiba.bgt_api_investment.dto.CustomerInvestmentResponse;
import com.ceiba.bgt_api_investment.dto.InvestmentDto;
import com.ceiba.bgt_api_investment.dto.InvestmentSummaryDto;
import com.ceiba.bgt_api_investment.exception.BusinessException;
import com.ceiba.bgt_api_investment.exception.ErrorMessages;
import com.ceiba.bgt_api_investment.exception.GlobalExceptionHandler;
import com.ceiba.bgt_api_investment.model.CustomerInvestmentStatus;
import com.ceiba.bgt_api_investment.security.JwtAuthenticationWebFilter;
import com.ceiba.bgt_api_investment.service.CustomerInvestmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Pruebas de integración de la capa web para {@link InvestmentController}.
 * Se usa @WebFluxTest para cargar solo el slice web, con seguridad mockeada
 * y el servicio substituido por un doble de prueba.
 */
@WebFluxTest(controllers = InvestmentController.class)
@Import({WebFluxTestSecurityConfig.class, GlobalExceptionHandler.class})
class InvestmentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CustomerInvestmentService customerInvestmentService;

    @MockitoBean
    private JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    /**
     * Configura el mock del filtro JWT para inyectar la autenticación de prueba
     * ("testuser") en el ReactiveSecurityContextHolder vía contextWrite.
     * Se ejecuta DENTRO de la cadena de seguridad (posición AUTHENTICATION),
     * replicando exactamente lo que hace el filtro real con un JWT válido.
     */
    @BeforeEach
    void setUpFilterWithAuth() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("testuser", null, Collections.emptyList());

        when(jwtAuthenticationWebFilter.filter(any(ServerWebExchange.class), any(WebFilterChain.class)))
                .thenAnswer(inv -> {
                    ServerWebExchange exchange = inv.getArgument(0);
                    WebFilterChain chain = inv.getArgument(1);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                });
    }

    // ══════════════════════════════════════════════════════════════════════
    // GET /investments/catalog
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /investments/catalog")
    class GetCatalog {

        @Test
        @DisplayName("Retorna 200 con la lista de fondos disponibles")
        void returns200WithCatalogList() {
            InvestmentDto dto = new InvestmentDto();
            dto.setId("inv-001");
            dto.setName("FIC Renta Fija");
            dto.setMinAmount(new BigDecimal("100.00"));
            dto.setCategory("RENTA_FIJA");

            when(customerInvestmentService.getCatalog()).thenReturn(Flux.just(dto));

            webTestClient.get()
                    .uri("/investments/catalog")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(ResponseMessages.GET_CATALOG_OK)
                    .jsonPath("$.data").isArray()
                    .jsonPath("$.data[0].id").isEqualTo("inv-001")
                    .jsonPath("$.data[0].name").isEqualTo("FIC Renta Fija")
                    .jsonPath("$.data[0].category").isEqualTo("RENTA_FIJA");
        }

        @Test
        @DisplayName("Retorna 200 con lista vacía cuando no hay fondos")
        void returns200WithEmptyList() {
            when(customerInvestmentService.getCatalog()).thenReturn(Flux.empty());

            webTestClient.get()
                    .uri("/investments/catalog")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(ResponseMessages.GET_CATALOG_OK)
                    .jsonPath("$.data").isEmpty();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // GET /investments
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /investments")
    class GetInvestments {

        @Test
        @DisplayName("Retorna 200 con las inversiones del cliente autenticado")
        void returns200WithCustomerInvestments() {
            InvestmentSummaryDto dto = new InvestmentSummaryDto();
            dto.setIdCustomerInvestment("ci-001");
            dto.setIdInvestment("inv-001");
            dto.setNameInvestment("FIC Renta Fija");
            dto.setInvestmentAmount(new BigDecimal("500.00"));
            dto.setStatus(CustomerInvestmentStatus.ACTIVE);
            dto.setOpenedAt(LocalDateTime.now());

            when(customerInvestmentService.getInvestments("testuser")).thenReturn(Flux.just(dto));

            webTestClient
                    .get()
                    .uri("/investments")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(ResponseMessages.GET_INVESTMENTS_OK)
                    .jsonPath("$.data[0].idCustomerInvestment").isEqualTo("ci-001")
                    .jsonPath("$.data[0].nameInvestment").isEqualTo("FIC Renta Fija")
                    .jsonPath("$.data[0].status").isEqualTo(CustomerInvestmentStatus.ACTIVE);
        }

        @Test
        @DisplayName("Retorna 404 cuando el cliente no existe en la base de datos")
        void returns404WhenCustomerNotFound() {
            when(customerInvestmentService.getInvestments("testuser"))
                    .thenReturn(Flux.error(new IllegalArgumentException(
                            String.format(ErrorMessages.CUSTOMER_NOT_FOUND, "testuser"))));

            webTestClient
                    .get()
                    .uri("/investments")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(
                            String.format(ErrorMessages.CUSTOMER_NOT_FOUND, "testuser"));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // POST /investments/subscribe
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /investments/subscribe")
    class Subscribe {

        private CustomerInvestmentRequest buildRequest() {
            CustomerInvestmentRequest req = new CustomerInvestmentRequest();
            req.setInvestment("inv-001");
            req.setAmount(new BigDecimal("500.00"));
            req.setNotificationSms(false);
            req.setNotificationEmail(false);
            return req;
        }

        @Test
        @DisplayName("Retorna 201 cuando la suscripción se crea correctamente")
        void returns201OnSuccessfulSubscription() {
            CustomerInvestmentResponse response = new CustomerInvestmentResponse();
            response.setInvestment("inv-001");
            response.setAmount(new BigDecimal("500.00"));
            response.setStatus(CustomerInvestmentStatus.ACTIVE);
            response.setOpenedAt(LocalDateTime.now());

            when(customerInvestmentService.subscribe(anyString(), any(), anyString()))
                    .thenReturn(Mono.just(response));

            webTestClient
                    .post()
                    .uri("/investments/subscribe")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(ResponseMessages.SUBSCRIBE_OK)
                    .jsonPath("$.data.investment").isEqualTo("inv-001")
                    .jsonPath("$.data.status").isEqualTo(CustomerInvestmentStatus.ACTIVE);
        }

        @Test
        @DisplayName("Retorna 400 cuando el monto es menor al mínimo del fondo")
        void returns400WhenAmountBelowMinimum() {
            when(customerInvestmentService.subscribe(anyString(), any(), anyString()))
                    .thenReturn(Mono.error(new BusinessException(ErrorMessages.AMOUNT_BELOW_MINIMUM)));

            webTestClient
                    .post()
                    .uri("/investments/subscribe")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest())
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(ErrorMessages.AMOUNT_BELOW_MINIMUM);
        }

        @Test
        @DisplayName("Retorna 400 cuando el cliente no tiene saldo suficiente")
        void returns400WhenInsufficientBalance() {
            String errorMsg = String.format(ErrorMessages.INSUFFICIENT_BALANCE, "FIC Renta Fija");

            when(customerInvestmentService.subscribe(anyString(), any(), anyString()))
                    .thenReturn(Mono.error(new BusinessException(errorMsg)));

            webTestClient
                    .post()
                    .uri("/investments/subscribe")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest())
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(errorMsg);
        }

        @Test
        @DisplayName("Retorna 404 cuando el fondo de inversión no existe")
        void returns404WhenInvestmentNotFound() {
            when(customerInvestmentService.subscribe(anyString(), any(), anyString()))
                    .thenReturn(Mono.error(new IllegalArgumentException(
                            String.format(ErrorMessages.INVESTMENT_NOT_FOUND, "inv-001"))));

            webTestClient
                    .post()
                    .uri("/investments/subscribe")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(buildRequest())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // PUT /investments/unsubscribe/{id}
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PUT /investments/unsubscribe/{id}")
    class Unsubscribe {

        @Test
        @DisplayName("Retorna 200 cuando la suscripción se cancela correctamente")
        void returns200OnSuccessfulUnsubscription() {
            CustomerInvestmentResponse response = new CustomerInvestmentResponse();
            response.setInvestment("inv-001");
            response.setAmount(new BigDecimal("500.00"));
            response.setStatus(CustomerInvestmentStatus.CANCELLED);
            response.setOpenedAt(LocalDateTime.now().minusDays(5));
            response.setClosedAt(LocalDateTime.now());

            when(customerInvestmentService.unsubscribe("testuser", "ci-001"))
                    .thenReturn(Mono.just(response));

            webTestClient
                    .put()
                    .uri("/investments/unsubscribe/ci-001")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(ResponseMessages.UNSUBSCRIBE_OK)
                    .jsonPath("$.data.status").isEqualTo(CustomerInvestmentStatus.CANCELLED)
                    .jsonPath("$.data.closedAt").isNotEmpty();
        }

        @Test
        @DisplayName("Retorna 400 cuando la suscripción ya estaba cancelada")
        void returns400WhenAlreadyCancelled() {
            when(customerInvestmentService.unsubscribe("testuser", "ci-001"))
                    .thenReturn(Mono.error(new BusinessException(ErrorMessages.SUBSCRIPTION_ALREADY_CANCELLED)));

            webTestClient
                    .put()
                    .uri("/investments/unsubscribe/ci-001")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(ErrorMessages.SUBSCRIPTION_ALREADY_CANCELLED);
        }

        @Test
        @DisplayName("Retorna 400 cuando la suscripción no pertenece al cliente autenticado")
        void returns400WhenSubscriptionNotOwned() {
            when(customerInvestmentService.unsubscribe("testuser", "ci-001"))
                    .thenReturn(Mono.error(new BusinessException(ErrorMessages.SUBSCRIPTION_NOT_OWNER)));

            webTestClient
                    .put()
                    .uri("/investments/unsubscribe/ci-001")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo(ErrorMessages.SUBSCRIPTION_NOT_OWNER);
        }

        @Test
        @DisplayName("Retorna 404 cuando la suscripción no existe")
        void returns404WhenSubscriptionNotFound() {
            when(customerInvestmentService.unsubscribe("testuser", "ci-001"))
                    .thenReturn(Mono.error(new IllegalArgumentException(
                            String.format(ErrorMessages.SUBSCRIPTION_NOT_FOUND, "ci-001"))));

            webTestClient
                    .put()
                    .uri("/investments/unsubscribe/ci-001")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}
