package com.ceiba.bgt_api_auth.repository;

import com.ceiba.bgt_api_auth.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {

    Mono<Customer> findByUsername(String username);
}
