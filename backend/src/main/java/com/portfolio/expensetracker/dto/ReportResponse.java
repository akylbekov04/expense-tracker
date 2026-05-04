package com.portfolio.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReportResponse(
        String period,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal total,
        List<CategoryTotalResponse> byCategory,
        List<TrendPointResponse> trend
) {
}
