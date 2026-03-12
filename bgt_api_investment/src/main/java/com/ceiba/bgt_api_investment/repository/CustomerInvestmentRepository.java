package com.ceiba.bgt_api_investment.repository;

import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Repositorio reactivo para la colección "customer_investment" en MongoDB.
 */
public interface CustomerInvestmentRepository extends ReactiveMongoRepository<CustomerInvestment, String> {

    Flux<CustomerInvestment> findByIdCustomer(ObjectId idCustomer);
}
