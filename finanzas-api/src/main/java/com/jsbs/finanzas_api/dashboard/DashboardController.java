package com.jsbs.finanzas_api.dashboard;

import com.jsbs.finanzas_api.dashboard.dto.MonthlySummaryResponse;
import com.jsbs.finanzas_api.dashboard.dto.ExpenseByCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/monthly-summary")
    public List<MonthlySummaryResponse> getMonthlySummaryByYear(
            @RequestParam Integer year
    ) {
        return dashboardService.getMonthlySummaryByYear(year);
    }

    @GetMapping("/expenses-by-category")
    public List<ExpenseByCategoryResponse> getExpensesByCategory(
            @RequestParam Integer year
    ) {
        return dashboardService.getExpensesByCategory(year);
    }
}