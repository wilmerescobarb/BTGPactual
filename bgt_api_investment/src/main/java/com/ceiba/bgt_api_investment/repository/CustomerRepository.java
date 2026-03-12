package com.ceiba.bgt_api_investment.repository;

import com.ceiba.bgt_api_investment.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para la colección "customer" en MongoDB.
 */
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    Mono<Customer> findByUsername(String username);
}
