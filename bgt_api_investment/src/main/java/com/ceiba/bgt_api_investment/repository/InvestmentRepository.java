package com.ceiba.bgt_api_investment.repository;

import com.ceiba.bgt_api_investment.model.Investment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Repositorio reactivo para la tabla "investment".
 */
public interface InvestmentRepository extends ReactiveCrudRepository<Investment, Integer> {
}
