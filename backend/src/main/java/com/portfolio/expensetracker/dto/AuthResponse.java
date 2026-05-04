package com.portfolio.expensetracker.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UserProfileResponse user
) {
}
