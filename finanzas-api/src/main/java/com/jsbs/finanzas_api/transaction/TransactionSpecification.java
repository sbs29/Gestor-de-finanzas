package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.category.CategoryType;
import com.jsbs.finanzas_api.user.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> belongsToUser(User user) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Transaction> hasCategoryType(CategoryType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("type"), type);
    }

    public static Specification<Transaction> hasDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("date"), start, end);
    }
}