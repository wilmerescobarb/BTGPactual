package com.ceiba.bgt_api_investment.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * Entidad que mapea la colección "investment" en MongoDB.
 */
@Data
@Document("investment")
public class Investment {

    @Id
    private ObjectId id;

    private String name;

    @Field("min_amount")
    private BigDecimal minAmount;

    private String category;
}
