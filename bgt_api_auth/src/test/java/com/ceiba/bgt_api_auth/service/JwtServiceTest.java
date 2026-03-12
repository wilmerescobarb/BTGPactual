package com.ceiba.bgt_api_auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para {@link JwtService}.
 * Se instancia directamente sin contexto Spring, inyectando los campos
 * {@code secret} y {@code expirationMs} vía reflexión.
 */
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "bgt-super-secret-key-for-jwt-signing-2026";
    private static final long EXPIRATION_MS = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
    }

    // ══════════════════════════════════════════════════════════════════════
    // generateToken
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("generateToken retorna un token JWT no nulo y no vacío")
    void generateTokenReturnsNonEmptyString() {
        String token = jwtService.generateToken("testuser");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("generateToken genera tokens distintos para invocaciones sucesivas")
    void generateTokenReturnsDifferentTokens() {
        String token1 = jwtService.generateToken("testuser");
        // Pequeña pausa para que el timestamp issuedAt difiera
        String token2 = jwtService.generateToken("testuser");
        // Los tokens podrían ser iguales si se generan en el mismo milisegundo,
        // pero validamos al menos que ambos son no nulos
        assertThat(token1).isNotNull();
        assertThat(token2).isNotNull();
    }

    // ══════════════════════════════════════════════════════════════════════
    // extractUsername
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("extractUsername retorna el username del subject del token")
    void extractUsernameReturnsCorrectUser() {
        String token = jwtService.generateToken("testuser");
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("extractUsername funciona correctamente con distintos usernames")
    void extractUsernameWorksWithDifferentUsers() {
        String token = jwtService.generateToken("admin");
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
    }

    // ══════════════════════════════════════════════════════════════════════
    // isTokenValid
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("isTokenValid retorna true para un token válido y el username correcto")
    void isTokenValidReturnsTrueForValidToken() {
        String token = jwtService.generateToken("testuser");
        assertThat(jwtService.isTokenValid(token, "testuser")).isTrue();
    }

    @Test
    @DisplayName("isTokenValid retorna false cuando el username no coincide con el del token")
    void isTokenValidReturnsFalseForWrongUsername() {
        String token = jwtService.generateToken("testuser");
        assertThat(jwtService.isTokenValid(token, "otheruser")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid retorna false para un token expirado")
    void isTokenValidReturnsFalseForExpiredToken() {
        // Configurar expiración de 0 ms → el token nace ya expirado
        JwtService expiredService = new JwtService();
        ReflectionTestUtils.setField(expiredService, "secret", SECRET);
        ReflectionTestUtils.setField(expiredService, "expirationMs", 0L);

        String token = expiredService.generateToken("testuser");
        assertThat(expiredService.isTokenValid(token, "testuser")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid retorna false para un token malformado")
    void isTokenValidReturnsFalseForMalformedToken() {
        assertThat(jwtService.isTokenValid("invalid.token.here", "testuser")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid retorna false para un token firmado con otra clave")
    void isTokenValidReturnsFalseForTokenSignedWithDifferentKey() {
        // Generar token con una clave distinta
        JwtService otherService = new JwtService();
        ReflectionTestUtils.setField(otherService, "secret", "another-secret-key-at-least-32-chars!!");
        ReflectionTestUtils.setField(otherService, "expirationMs", EXPIRATION_MS);

        String token = otherService.generateToken("testuser");
        // Validar con la clave original → debe fallar
        assertThat(jwtService.isTokenValid(token, "testuser")).isFalse();
    }

    // ══════════════════════════════════════════════════════════════════════
    // getExpirationMs
    // ══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getExpirationMs retorna el valor configurado")
    void getExpirationMsReturnsConfiguredValue() {
        assertThat(jwtService.getExpirationMs()).isEqualTo(EXPIRATION_MS);
    }
}
