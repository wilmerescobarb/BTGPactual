package com.ceiba.bgt_api_investment.dto;

import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import com.ceiba.bgt_api_investment.model.Investment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que representa el resumen de una inversión del customer
 * para el listado GET /investments.
 */
@Data
public class InvestmentSummaryDto {

    private String idCustomerInvestment;
    private String idInvestment;
    private String nameInvestment;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal investmentAmount;
    private String status;

    public static InvestmentSummaryDto from(CustomerInvestment ci, Investment investment) {
        InvestmentSummaryDto dto = new InvestmentSummaryDto();
        dto.setIdCustomerInvestment(ci.getId());
        dto.setIdInvestment(ci.getIdInvestment() != null ? ci.getIdInvestment().toHexString() : null);
        dto.setNameInvestment(investment.getName());
        dto.setOpenedAt(ci.getOpenedAt());
        dto.setClosedAt(ci.getClosedAt());
        dto.setInvestmentAmount(ci.getInvestedAmount());
        dto.setStatus(ci.getStatus());
        return dto;
    }
}
