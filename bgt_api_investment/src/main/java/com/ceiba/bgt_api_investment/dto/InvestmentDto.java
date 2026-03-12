package com.ceiba.bgt_api_investment.dto;

import com.ceiba.bgt_api_investment.model.Investment;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa un fondo de inversión del catálogo.
 */
@Data
public class InvestmentDto {

    private String id;
    private String name;
    private BigDecimal minAmount;
    private String category;

    public static InvestmentDto from(Investment investment) {
        InvestmentDto dto = new InvestmentDto();
        dto.setId(investment.getId().toHexString());
        dto.setName(investment.getName());
        dto.setMinAmount(investment.getMinAmount());
        dto.setCategory(investment.getCategory());
        return dto;
    }
}
