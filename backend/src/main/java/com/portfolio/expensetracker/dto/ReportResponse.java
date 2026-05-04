package com.portfolio.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReportResponse(
        @Schema(description = "Period")
        String period,
        @Schema(description = "Start date")
        LocalDate startDate,
        @Schema(description = "End date")
        LocalDate endDate,
        @Schema(description = "Total amount")
        BigDecimal total,
        List<CategoryTotalResponse> byCategory,
        List<TrendPointResponse> trend
) {
}
