package com.ceiba.bgt_api_auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("customer")
public class Customer {

    @Id
    private Long id;

    private String names;

    private String lastnames;

    private LocalDate birthday;

    @Column("document_type")
    private String documentType;

    @Column("document_number")
    private String documentNumber;

    private String username;

    @Column("pass_user")
    private String passUser;

    private BigDecimal amount;

    @Column("created_at")
    private LocalDate createdAt;
}
