package com.ceiba.bgt_api_investment.dto;

import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de salida que representa la confirmación de una suscripción.
 * Contiene todos los campos devueltos al cliente tras crear o cancelar una suscripción.
 */
@Data
public class CustomerInvestmentResponse {

    private String investment;

    private BigDecimal amount;

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    private String status;

    public static CustomerInvestmentResponse from(CustomerInvestment entity) {
        CustomerInvestmentResponse response = new CustomerInvestmentResponse();
        response.setInvestment(entity.getIdInvestment() != null ? entity.getIdInvestment().toHexString() : null);
        response.setAmount(entity.getInvestedAmount());
        response.setOpenedAt(entity.getOpenedAt());
        response.setClosedAt(entity.getClosedAt());
        response.setStatus(entity.getStatus());
        return response;
    }
}
