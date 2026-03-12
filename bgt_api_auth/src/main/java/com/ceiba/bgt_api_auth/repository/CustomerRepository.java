package com.ceiba.bgt_api_auth.repository;

import com.ceiba.bgt_api_auth.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    Mono<Customer> findByUsername(String username);
}
