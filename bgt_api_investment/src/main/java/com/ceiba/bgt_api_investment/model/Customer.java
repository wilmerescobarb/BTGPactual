package com.ceiba.bgt_api_investment.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * Entidad que mapea la colección "customer" en MongoDB.
 * Solo se exponen los campos necesarios para este microservicio.
 */
@Data
@Document("customer")
public class Customer {

    @Id
    @Field("_id")
    private ObjectId id;
    private String username;
    private BigDecimal amount;
    private String email;
    private String cellphone;
}
