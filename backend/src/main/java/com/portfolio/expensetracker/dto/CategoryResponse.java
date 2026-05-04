package com.portfolio.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(
        @Schema(description = "Id")
        Long id,
        @Schema(description = "Name")
        String name,
        @Schema(description = "Color")
        String color
) {
}
