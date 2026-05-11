package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.category.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String description,
        LocalDateTime date,
        Long categoryId,
        String categoryName,
        CategoryType categoryType
) {
}
