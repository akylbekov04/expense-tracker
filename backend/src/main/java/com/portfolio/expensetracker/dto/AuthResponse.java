package com.portfolio.expensetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "Access Token")
        String accessToken,
        @Schema(description = "Refresh Token")
        String refreshToken,
        @Schema(description = "Token type")
        String tokenType,
        @Schema(description = "User")
        UserProfileResponse user
) {
}
