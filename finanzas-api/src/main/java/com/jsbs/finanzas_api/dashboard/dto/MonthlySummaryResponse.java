package com.jsbs.finanzas_api.dashboard.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        int month,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {
}