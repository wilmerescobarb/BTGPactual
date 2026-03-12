package com.ceiba.bgt_api_investment.repository;

import com.ceiba.bgt_api_investment.model.Customer;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Repositorio reactivo para la tabla "customer".
 */
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    Mono<Customer> findByUsername(String username);

    @Modifying
    @Query("UPDATE customer SET amount = :amount WHERE id = :id")
    Mono<Integer> updateAmount(Integer id, BigDecimal amount);
}
