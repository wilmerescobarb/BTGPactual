package com.ceiba.bgt_api_investment.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que mapea la tabla "customer_investment".
 */
@Data
@Builder
@Table("customer_investment")
public class CustomerInvestment {

    @Id
    @Column("id_customer_investment")
    private Integer id;

    @Column("id_customer")
    private Integer idCustomer;

    @Column("id_investment")
    private Integer idInvestment;

    @Column("opened_at")
    private LocalDateTime openedAt;

    @Column("closed_at")
    private LocalDateTime closedAt;

    @Column("invested_amount")
    private BigDecimal investedAmount;

    private String status;
}
