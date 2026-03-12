package com.ceiba.bgt_api_notification.controller;

import com.ceiba.bgt_api_notification.dto.EmailRequest;
import com.ceiba.bgt_api_notification.dto.NotificationResponse;
import com.ceiba.bgt_api_notification.dto.SmsRequest;
import com.ceiba.bgt_api_notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NotificationResponse> sendEmail(@RequestBody EmailRequest request) {
        return notificationService.sendEmail(request);
    }

    @PostMapping("/sms")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NotificationResponse> sendSms(@RequestBody SmsRequest request) {
        return notificationService.sendSms(request);
    }
}
