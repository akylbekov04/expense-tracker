package com.portfolio.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        @Schema(description = "Id")
        Long id,
        @Schema(description = "Amount")
        BigDecimal amount,
        @Schema(description = "Expense date")
        LocalDate expenseDate,
        @Schema(description = "Title")
        String title,
        @Schema(description = "Note")
        String note,
        @Schema(description = "Category")
        CategoryResponse category
) {
}
