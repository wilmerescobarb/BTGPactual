package com.ceiba.bgt_api_auth.controller;

import com.ceiba.bgt_api_auth.config.SecurityConfig;
import com.ceiba.bgt_api_auth.model.AuthResponse;
import com.ceiba.bgt_api_auth.model.LoginRequest;
import com.ceiba.bgt_api_auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas de integración de la capa web para {@link AuthController}.
 * Se usa @WebFluxTest para cargar solo el slice web.
 * Se importa SecurityConfig para que /auth/** esté permitido públicamente.
 */
@WebFluxTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthService authService;

    // ══════════════════════════════════════════════════════════════════════
    // POST /auth/login – Éxito
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /auth/login exitoso retorna 200 con AuthResponse completo")
    void loginSuccessReturns200WithAuthResponse() {
        AuthResponse response = AuthResponse.builder()
                .token("jwt-token-123")
                .tokenType("Bearer")
                .expiresIn(3600000L)
                .username("testuser")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest("testuser", "password123"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("jwt-token-123")
                .jsonPath("$.tokenType").isEqualTo("Bearer")
                .jsonPath("$.expiresIn").isEqualTo(3600000)
                .jsonPath("$.username").isEqualTo("testuser");
    }

    // ══════════════════════════════════════════════════════════════════════
    // POST /auth/login – Usuario no encontrado
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /auth/login con usuario inexistente retorna 401 Unauthorized")
    void loginUserNotFoundReturns401() {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Usuario no encontrado: unknown")));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest("unknown", "password123"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }

    // ══════════════════════════════════════════════════════════════════════
    // POST /auth/login – Contraseña incorrecta
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /auth/login con contraseña incorrecta retorna 401 Unauthorized")
    void loginWrongPasswordReturns401() {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Contraseña incorrecta para: testuser")));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest("testuser", "wrongpassword"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty();
    }

    // ══════════════════════════════════════════════════════════════════════
    // POST /auth/login – Validar que devuelve Content-Type JSON
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /auth/login exitoso retorna Content-Type application/json")
    void loginSuccessReturnsJsonContentType() {
        AuthResponse response = AuthResponse.builder()
                .token("token-abc")
                .tokenType("Bearer")
                .expiresIn(3600000L)
                .username("admin")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest("admin", "admin123"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }
}
