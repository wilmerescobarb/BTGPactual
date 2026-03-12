package com.ceiba.bgt_api_customer.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO con los datos necesarios para registrar un nuevo cliente.
 */
@Data
public class RegisterRequest {

    private String names;
    private String lastnames;
    private LocalDate birthday;
    private String documentType;
    private String documentNumber;
    private String cellphone;
    private String email;
    private String username;

    /**
     * Contraseña en texto plano; se almacenará como hash bcrypt.
     */
    private String passUser;
}
