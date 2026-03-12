package com.ceiba.bgt_api_investment.repository;

import com.ceiba.bgt_api_investment.model.Investment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Repositorio reactivo para la colección "investment" en MongoDB.
 */
public interface InvestmentRepository extends ReactiveMongoRepository<Investment, String> {
}
