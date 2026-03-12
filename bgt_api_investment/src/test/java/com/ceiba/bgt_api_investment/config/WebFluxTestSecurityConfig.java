package com.ceiba.bgt_api_investment.config;

import com.ceiba.bgt_api_investment.security.JwtAuthenticationWebFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad para tests @WebFluxTest.
 *
 * Reemplaza {@code SecurityConfig} de producción:
 * - CSRF deshabilitado.
 * - {@code permitAll()} en lugar de {@code authenticated()}.
 * - Registra el mock de {@link JwtAuthenticationWebFilter} en la posición AUTHENTICATION
 *   de la cadena de seguridad, para que su {@code contextWrite} inyecte la autenticación
 *   DENTRO de la cadena, haciendo visible el principal a {@code @AuthenticationPrincipal}.
 */
@TestConfiguration
@EnableWebFluxSecurity
public class WebFluxTestSecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    public WebFluxTestSecurityConfig(JwtAuthenticationWebFilter jwtAuthenticationWebFilter) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
    }

    @Bean
    public SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
