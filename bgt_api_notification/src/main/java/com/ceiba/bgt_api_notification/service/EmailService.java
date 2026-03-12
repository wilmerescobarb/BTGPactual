package com.ceiba.bgt_api_notification.service;

import com.ceiba.bgt_api_notification.dto.EmailRequest;
import com.ceiba.bgt_api_notification.dto.NotificationResponse;
import com.ceiba.bgt_api_notification.exception.NotificationDeliveryException;
import com.ceiba.bgt_api_notification.exception.NotificationException;
import com.ceiba.bgt_api_notification.util.ErrorMessages;
import com.ceiba.bgt_api_notification.util.MaskingUtils;
import com.ceiba.bgt_api_notification.util.NotificationConstants;
import com.ceiba.bgt_api_notification.util.ResponseMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements NotificationChannel<EmailRequest> {

    private static final String LOG_EMAIL_SENT = "Email enviado a {}";

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Override
    public Mono<NotificationResponse> send(EmailRequest request) {
        return Mono.fromCallable(() -> {
                    SimpleMailMessage mail = new SimpleMailMessage();
                    mail.setFrom(fromEmail);
                    mail.setTo(request.getTo());
                    mail.setSubject(request.getSubject());
                    mail.setText(request.getMessage());
                    mailSender.send(mail);
                    log.info(LOG_EMAIL_SENT, MaskingUtils.maskEmail(request.getTo()));
                    return NotificationResponse.builder()
                            .status(NotificationConstants.STATUS_SENT)
                            .detail(ResponseMessages.EMAIL_SENT + request.getTo())
                            .build();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(ex -> !(ex instanceof NotificationException),
                        ex -> new NotificationDeliveryException(ErrorMessages.EMAIL_DELIVERY_ERROR + ex.getMessage(), ex));
    }
}
