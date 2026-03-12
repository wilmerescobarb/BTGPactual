package com.ceiba.bgt_api_investment.dto;

import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO usado tanto para el request (investment + amount) como para
 * la respuesta (todos los campos confirmados de la suscripción).
 */
@Data
public class CustomerInvestmentDto {

    private String investment;

    private BigDecimal amount;

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    private String status;

    public static CustomerInvestmentDto from(CustomerInvestment entity) {
        CustomerInvestmentDto dto = new CustomerInvestmentDto();
        dto.setInvestment(entity.getIdInvestment() != null ? entity.getIdInvestment().toHexString() : null);
        dto.setAmount(entity.getInvestedAmount());
        dto.setOpenedAt(entity.getOpenedAt());
        dto.setClosedAt(entity.getClosedAt());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
