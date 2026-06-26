package com.jsbs.finanzas_api.dashboard.dto;

import java.math.BigDecimal;

public record ExpenseByCategoryResponse(
        String category,
        BigDecimal amount
) {
}