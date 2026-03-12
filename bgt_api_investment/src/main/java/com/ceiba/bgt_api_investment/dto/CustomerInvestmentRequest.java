package com.ceiba.bgt_api_investment.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de entrada para suscribirse a un fondo de inversión.
 * Solo contiene los campos que el cliente debe enviar en el request.
 */
@Data
public class CustomerInvestmentRequest {

    private String investment;

    private BigDecimal amount;
}
