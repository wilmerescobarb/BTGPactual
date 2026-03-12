package com.ceiba.bgt_api_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {

    /** Número de teléfono en formato E.164, ej: +573001234567 (requerido) */
    private String phoneNumber;

    /** Cuerpo del mensaje SMS (requerido) */
    private String message;
}
