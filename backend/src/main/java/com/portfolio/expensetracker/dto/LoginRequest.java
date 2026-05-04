package com.portfolio.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request")
public record LoginRequest(
        @Email(message = "Wrong email format")
        @NotBlank(message = "Email cannot be empty")
        @Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @NotBlank(message = "Password cannot be empty")
        @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
