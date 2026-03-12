package com.ceiba.bgt_api_customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO con los campos del cliente que se exponen en la respuesta del GET /customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private String id;
    private String username;
    private BigDecimal amount;
    private String names;
    private String lastnames;
}
