package com.portfolio.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @DecimalMin(value = "0.01", message = "Minimum amount is 0.01")
        @Schema(description = "Amount")
        BigDecimal amount,
        @Schema(description = "Expense date", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Expense date cannot be null") LocalDate expenseDate,
        @Schema(description = "Title", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Title cannot be empty") String title,
        String note,
        @Schema(description = "Category id", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long categoryId
) {
}
