package com.ceiba.bgt_api_investment.model;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que mapea la colección "customer_investment" en MongoDB.
 */
@Data
@Builder
@Document("customer_investment")
public class CustomerInvestment {

    @Id
    private String id;

    @Field("id_customer")
    private ObjectId idCustomer;

    @Field("id_investment")
    private ObjectId idInvestment;

    @Field("opened_at")
    private LocalDateTime openedAt;

    @Field("closed_at")
    private LocalDateTime closedAt;

    @Field("invested_amount")
    private BigDecimal investedAmount;

    private String status;
}
