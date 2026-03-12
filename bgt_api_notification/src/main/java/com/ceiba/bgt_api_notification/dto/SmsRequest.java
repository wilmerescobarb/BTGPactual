package com.ceiba.bgt_api_notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ceiba.bgt_api_notification.util.ErrorMessages.*;
import static com.ceiba.bgt_api_notification.util.NotificationConstants.E164_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {

    /** Número de teléfono en formato E.164, ej: +573001234567 (requerido) */
    @NotBlank(message = SMS_PHONE_REQUIRED)
    @Pattern(
            regexp = E164_PATTERN,
            message = SMS_PHONE_INVALID
    )
    private String phoneNumber;

    /** Cuerpo del mensaje SMS (requerido) */
    @NotBlank(message = SMS_MESSAGE_REQUIRED)
    private String message;
}
