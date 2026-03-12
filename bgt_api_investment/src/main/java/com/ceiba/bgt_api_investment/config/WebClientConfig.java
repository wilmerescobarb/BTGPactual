package com.ceiba.bgt_api_investment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración del WebClient reactivo utilizado para consumir APIs externas
 * (p. ej., el microservicio de notificaciones).
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
