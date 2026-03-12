package com.ceiba.bgt_api_notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ceiba.bgt_api_notification.util.ErrorMessages.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    /** Correo electrónico del destinatario (requerido) */
    @NotBlank(message = EMAIL_TO_REQUIRED)
    @Email(message = EMAIL_TO_INVALID)
    private String to;

    /** Asunto del correo (requerido) */
    @NotBlank(message = EMAIL_SUBJECT_REQUIRED)
    private String subject;

    /** Cuerpo del mensaje (requerido) */
    @NotBlank(message = EMAIL_MESSAGE_REQUIRED)
    private String message;
}
