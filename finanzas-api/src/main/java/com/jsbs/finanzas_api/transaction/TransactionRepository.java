package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.category.CategoryType;
import com.jsbs.finanzas_api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    List<Transaction> findByUser(User user);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    List<Transaction> findByUserAndDateBetween(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );

    Page<Transaction> findByUser(User user, Pageable pageable);
}