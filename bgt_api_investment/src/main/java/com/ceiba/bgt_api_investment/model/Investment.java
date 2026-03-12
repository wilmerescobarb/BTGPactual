package com.ceiba.bgt_api_investment.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Entidad que mapea la tabla "investment" en PostgreSQL.
 */
@Data
@Table("investment")
public class Investment {

    @Id
    private Integer id;

    private String name;

    @Column("min_amount")
    private BigDecimal minAmount;

    private String category;
}
