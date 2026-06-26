package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.dashboard.dto.MonthlySummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserIdAndDateBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsByCategoryId(Long categoryId);

    @Query("""
        SELECT new com.jsbs.finanzas_api.dashboard.dto.MonthlySummaryResponse(
            MONTH(t.date),
            COALESCE(SUM(CASE WHEN t.category.type = 'INCOME' THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.category.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.category.type = 'INCOME' THEN t.amount ELSE 0 END), 0)
            -
            COALESCE(SUM(CASE WHEN t.category.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)
        )
        FROM Transaction t
        WHERE t.user.id = :userId
        AND YEAR(t.date) = :year
        GROUP BY MONTH(t.date)
        ORDER BY MONTH(t.date)
        """)
    List<MonthlySummaryResponse> getMonthlySummaryByYear(
            Long userId,
            Integer year
    );
}