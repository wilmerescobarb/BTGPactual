package com.ceiba.bgt_api_auth.service;

import com.ceiba.bgt_api_auth.model.Customer;
import com.ceiba.bgt_api_auth.model.LoginRequest;
import com.ceiba.bgt_api_auth.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link AuthService}.
 * Se usa Mockito para mockear CustomerRepository, JwtService y PasswordEncoder.
 * Se valida el flujo reactivo con StepVerifier.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Customer customer;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId("c-001");
        customer.setUsername("testuser");
        customer.setPassUser("$2a$10$hashedPassword");

        loginRequest = new LoginRequest("testuser", "rawPassword");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Login exitoso
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Login exitoso retorna AuthResponse con token, tipo Bearer, expiración y username")
    void loginSuccessReturnsAuthResponse() {
        when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
        when(passwordEncoder.matches("rawPassword", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn("jwt-token-123");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        StepVerifier.create(authService.login(loginRequest))
                .assertNext(response -> {
                    assertThat(response.getToken()).isEqualTo("jwt-token-123");
                    assertThat(response.getTokenType()).isEqualTo("Bearer");
                    assertThat(response.getExpiresIn()).isEqualTo(3600000L);
                    assertThat(response.getUsername()).isEqualTo("testuser");
                })
                .verifyComplete();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Usuario no encontrado
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Login con usuario inexistente emite error 'Usuario no encontrado'")
    void loginUserNotFoundThrowsError() {
        when(customerRepository.findByUsername("unknown")).thenReturn(Mono.empty());

        LoginRequest request = new LoginRequest("unknown", "anyPassword");

        StepVerifier.create(authService.login(request))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException
                                && ex.getMessage().contains("Usuario no encontrado"))
                .verify();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Contraseña incorrecta
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Login con contraseña incorrecta emite error 'Contraseña incorrecta'")
    void loginWrongPasswordThrowsError() {
        when(customerRepository.findByUsername("testuser")).thenReturn(Mono.just(customer));
        when(passwordEncoder.matches("wrongPass", "$2a$10$hashedPassword")).thenReturn(false);

        LoginRequest request = new LoginRequest("testuser", "wrongPass");

        StepVerifier.create(authService.login(request))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException
                                && ex.getMessage().contains("Contraseña incorrecta"))
                .verify();
    }
}
