package com.ceiba.bgt_api_investment.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Entidad que mapea la tabla "customer" en PostgreSQL.
 * Solo se exponen los campos necesarios para este microservicio.
 */
@Data
@Table("customer")
public class Customer {

    @Id
    private Integer id;
    private String username;
    private BigDecimal amount;
}
