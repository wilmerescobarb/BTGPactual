package com.ceiba.bgt_api_investment.service;

import com.ceiba.bgt_api_investment.dto.CustomerInvestmentRequest;
import com.ceiba.bgt_api_investment.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Cliente reactivo que llama al microservicio bgt_api_notification.
 * Los errores de notificación se absorben para no afectar el flujo principal.
 */
@Slf4j
@Service
public class NotificationWebClient {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");

    private static final String SMS_PATH          = "/api/notifications/sms";
    private static final String EMAIL_PATH        = "/api/notifications/email";
    private static final String EMAIL_SUBJECT     = "Apertura fondo de inversiones";
    private static final String MESSAGE_TEMPLATE  =
            "Se ha realizado la apertura de un nuevo fondo de inversión el día %s";
    private static final String COLOMBIA_PREFIX   = "+57";

    private final WebClient webClient;
    private final String notificationServiceUrl;

    public NotificationWebClient(WebClient webClient,
                                 @Value("${notification.service.url}") String notificationServiceUrl) {
        this.webClient = webClient;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    public Mono<Void> sendNotifications(Customer customer,
                                        CustomerInvestmentRequest request,
                                        String authHeader,
                                        LocalDateTime openedAt) {

        String message = String.format(MESSAGE_TEMPLATE, openedAt.format(DATE_FORMATTER));
        List<Mono<Void>> notifications = new ArrayList<>();

        if (request.isNotificationSms() && customer.getCellphone() != null) {
            Map<String, String> smsBody = Map.of(
                    "phoneNumber", toE164(customer.getCellphone()),
                    "message", message
            );
            notifications.add(post(SMS_PATH, smsBody, authHeader));
        }

        if (request.isNotificationEmail() && customer.getEmail() != null) {
            Map<String, String> emailBody = Map.of(
                    "to", customer.getEmail(),
                    "subject", EMAIL_SUBJECT,
                    "message", message
            );
            notifications.add(post(EMAIL_PATH, emailBody, authHeader));
        }

        return Mono.when(notifications);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Mono<Void> post(String path, Map<String, String> body, String authHeader) {
        return webClient.post()
                .uri(notificationServiceUrl + path)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(ex -> {
                    log.warn("Notification call to {} failed: {}", path, ex.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Convierte un número de celular al formato E.164.
     * Si ya comienza con '+' se devuelve tal cual; de lo contrario se asume
     * prefijo colombiano (+57).
     */
    private static String toE164(String cellphone) {
        String digits = cellphone.trim();
        if (digits.startsWith("+")) {
            return digits;
        }
        return COLOMBIA_PREFIX + digits;
    }
}
