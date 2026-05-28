package com.jsbs.finanzas_api.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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
}