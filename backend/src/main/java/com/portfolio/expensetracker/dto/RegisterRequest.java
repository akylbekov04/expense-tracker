package com.portfolio.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration request")
public record RegisterRequest(
        @NotBlank(message = "Name cannot be empty")
        @Schema(description = "Name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Email(message = "Wrong email format")
        @NotBlank(message = "Email cannot be empty")
        @Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Size(min = 6, max = 100)
        @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password cannot be empty")
        String password
) {
}
