package com.portfolio.expensetracker.dto;

import java.math.BigDecimal;

public record CategoryTotalResponse(
        String category,
        String color,
        BigDecimal total
) {
}
