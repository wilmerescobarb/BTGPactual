package com.ceiba.bgt_api_notification.service;

import com.ceiba.bgt_api_notification.dto.NotificationResponse;
import com.ceiba.bgt_api_notification.dto.SmsRequest;
import com.ceiba.bgt_api_notification.exception.NotificationDeliveryException;
import com.ceiba.bgt_api_notification.exception.NotificationException;
import com.ceiba.bgt_api_notification.util.ErrorMessages;
import com.ceiba.bgt_api_notification.util.MaskingUtils;
import com.ceiba.bgt_api_notification.util.NotificationConstants;
import com.ceiba.bgt_api_notification.util.ResponseMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService implements NotificationChannel<SmsRequest> {

    private static final String LOG_SMS_SENDING = "Enviando SMS a {}";
    private static final String LOG_SMS_SENT    = "SMS enviado. MessageId={}";

    private final SnsAsyncClient snsAsyncClient;

    @Override
    public Mono<NotificationResponse> send(SmsRequest request) {
        PublishRequest snsRequest = PublishRequest.builder()
                .phoneNumber(request.getPhoneNumber())
                .message(request.getMessage())
                .build();

        log.info(LOG_SMS_SENDING, MaskingUtils.maskPhone(request.getPhoneNumber()));

        return Mono.fromFuture(() -> snsAsyncClient.publish(snsRequest))
                .map(response -> NotificationResponse.builder()
                        .messageId(response.messageId())
                        .status(NotificationConstants.STATUS_SENT)
                        .detail(ResponseMessages.SMS_SENT + MaskingUtils.maskPhone(request.getPhoneNumber()))
                        .build())
                .doOnSuccess(r -> log.info(LOG_SMS_SENT, r.getMessageId()))
                .onErrorMap(ex -> !(ex instanceof NotificationException),
                            ex -> new NotificationDeliveryException(ErrorMessages.SMS_DELIVERY_ERROR + ex.getMessage(), ex));
    }
}
