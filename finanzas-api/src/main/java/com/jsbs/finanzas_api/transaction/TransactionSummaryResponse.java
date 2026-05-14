package com.jsbs.finanzas_api.transaction;

import java.math.BigDecimal;

public record TransactionSummaryResponse(
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {
}
