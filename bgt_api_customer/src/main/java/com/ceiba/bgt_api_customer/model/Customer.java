package com.ceiba.bgt_api_customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que mapea la colección "customer" en MongoDB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customer")
public class Customer {

    @Id
    @Field("_id")
    private String id;

    private String names;

    private String lastnames;

    private LocalDate birthday;

    @Field("document_type")
    private String documentType;

    @Field("document_number")
    private String documentNumber;

    private String cellphone;

    private String email;

    private String username;

    @Field("pass_user")
    private String passUser;

    private BigDecimal amount;

    @Field("created_at")
    private LocalDate createdAt;
}
