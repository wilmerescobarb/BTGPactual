package com.ceiba.bgt_api_notification.service;

import com.ceiba.bgt_api_notification.dto.EmailRequest;
import com.ceiba.bgt_api_notification.dto.NotificationResponse;
import com.ceiba.bgt_api_notification.dto.SmsRequest;
import com.ceiba.bgt_api_notification.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final SnsAsyncClient snsAsyncClient;

    @Value("${app.mail.from}")
    private String fromEmail;

    // ================================================================ EMAIL
    public Mono<NotificationResponse> sendEmail(EmailRequest request) {
        if (request.getTo() == null || request.getTo().isBlank()) {
            return Mono.error(new NotificationException("El campo 'to' es requerido"));
        }
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            return Mono.error(new NotificationException("El campo 'subject' es requerido"));
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return Mono.error(new NotificationException("El campo 'message' es requerido"));
        }

        return Mono.fromCallable(() -> {
                    SimpleMailMessage mail = new SimpleMailMessage();
                    mail.setFrom(fromEmail);
                    mail.setTo(request.getTo());
                    mail.setSubject(request.getSubject());
                    mail.setText(request.getMessage());
                    mailSender.send(mail);
                    log.info("Email enviado a {}", request.getTo());
                    return NotificationResponse.builder()
                            .status("SENT")
                            .detail("Email enviado correctamente a " + request.getTo())
                            .build();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(ex -> new NotificationException("Error al enviar email: " + ex.getMessage()));
    }


    public Mono<NotificationResponse> sendSms(SmsRequest request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()) {
            return Mono.error(new NotificationException("El campo 'phoneNumber' es requerido (formato E.164, ej: +573001234567)"));
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return Mono.error(new NotificationException("El campo 'message' es requerido"));
        }

        PublishRequest snsRequest = PublishRequest.builder()
                .phoneNumber(request.getPhoneNumber())
                .message(request.getMessage())
                .build();

        log.info("Enviando SMS a {}", request.getPhoneNumber());

        return Mono.fromFuture(() -> snsAsyncClient.publish(snsRequest))
                .map(response -> NotificationResponse.builder()
                        .messageId(response.messageId())
                        .status("SENT")
                        .detail("SMS enviado correctamente a " + request.getPhoneNumber())
                        .build())
                .doOnSuccess(r -> log.info("SMS enviado. MessageId={}", r.getMessageId()))
                .onErrorMap(ex -> new NotificationException("Error al enviar SMS: " + ex.getMessage()));
    }
}
