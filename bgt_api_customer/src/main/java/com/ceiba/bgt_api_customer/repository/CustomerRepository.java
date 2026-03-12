package com.ceiba.bgt_api_customer.repository;

import com.ceiba.bgt_api_customer.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para la colección "customer".
 */
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    /**
     * Busca un cliente por su nombre de usuario.
     */
    Mono<Customer> findByUsername(String username);
}
