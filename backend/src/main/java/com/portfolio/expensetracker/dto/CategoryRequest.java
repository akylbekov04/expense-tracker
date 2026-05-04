package com.portfolio.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(title = "Category request")
public record CategoryRequest(
        @NotBlank(message = "Name cannot be empty")
        @Schema(description = "Name")
        String name,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Wrong color pattern")
        @Schema(description = "Color")
        String color
) {
}
