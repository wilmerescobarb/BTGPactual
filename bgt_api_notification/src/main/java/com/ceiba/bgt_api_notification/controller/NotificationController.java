package com.ceiba.bgt_api_notification.controller;

import com.ceiba.bgt_api_notification.dto.EmailRequest;
import com.ceiba.bgt_api_notification.dto.NotificationResponse;
import com.ceiba.bgt_api_notification.dto.SmsRequest;
import com.ceiba.bgt_api_notification.service.EmailService;
import com.ceiba.bgt_api_notification.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;
    private final SmsService smsService;

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NotificationResponse> sendEmail(@Valid @RequestBody EmailRequest request) {
        return emailService.send(request);
    }

    @PostMapping("/sms")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NotificationResponse> sendSms(@Valid @RequestBody SmsRequest request) {
        return smsService.send(request);
    }
}
