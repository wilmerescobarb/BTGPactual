package com.ceiba.bgt_api_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    /** Correo electrónico del destinatario (requerido) */
    private String to;

    /** Asunto del correo (requerido) */
    private String subject;

    /** Cuerpo del mensaje (requerido) */
    private String message;
}
