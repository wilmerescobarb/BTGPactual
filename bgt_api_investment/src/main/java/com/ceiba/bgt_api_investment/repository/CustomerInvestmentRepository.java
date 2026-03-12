package com.ceiba.bgt_api_investment.repository;

import com.ceiba.bgt_api_investment.dto.InvestmentSummaryDto;
import com.ceiba.bgt_api_investment.model.CustomerInvestment;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Repositorio reactivo para la tabla "customer_investment".
 */
public interface CustomerInvestmentRepository extends ReactiveCrudRepository<CustomerInvestment, Integer> {

    @Query("""
            SELECT ci.id_customer_investment,
                   ci.id_investment,
                   i.name            AS name_investment,
                   ci.opened_at,
                   ci.closed_at,
                   ci.invested_amount AS investment_amount,
                   ci.status
            FROM customer_investment ci
            JOIN investment i  ON ci.id_investment = i.id
            JOIN customer   c  ON ci.id_customer   = c.id
            WHERE c.username = :username
            """)
    Flux<InvestmentSummaryDto> findInvestmentsByUsername(String username);

    @Modifying
    @Query("UPDATE customer_investment SET status = :status, closed_at = :closedAt WHERE id_customer_investment = :id")
    Mono<Integer> updateStatus(Integer id, String status, LocalDateTime closedAt);
}
