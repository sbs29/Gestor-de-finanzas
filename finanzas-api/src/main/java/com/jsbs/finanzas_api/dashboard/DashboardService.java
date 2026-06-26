package com.jsbs.finanzas_api.dashboard;

import com.jsbs.finanzas_api.dashboard.dto.MonthlySummaryResponse;
import com.jsbs.finanzas_api.security.CurrentUserService;
import com.jsbs.finanzas_api.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.jsbs.finanzas_api.dashboard.dto.ExpenseByCategoryResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public List<MonthlySummaryResponse> getMonthlySummaryByYear(Integer year) {
        Long userId = currentUserService.getCurrentUser().getId();

        List<MonthlySummaryResponse> summaries =
                transactionRepository.getMonthlySummaryByYear(userId, year);

        Map<Integer, MonthlySummaryResponse> summariesByMonth = summaries.stream()
                .collect(Collectors.toMap(
                        MonthlySummaryResponse::month,
                        summary -> summary
                ));

        return IntStream.rangeClosed(1, 12)
                .mapToObj(month -> summariesByMonth.getOrDefault(
                        month,
                        new MonthlySummaryResponse(
                                month,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO
                        )
                ))
                .toList();
    }

    public List<ExpenseByCategoryResponse> getExpensesByCategory(Integer year) {
        Long userId = currentUserService.getCurrentUser().getId();

        return transactionRepository.getExpensesByCategory(userId, year);
    }
}