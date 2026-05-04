package com.portfolio.expensetracker.dto;

public record UserProfileResponse(
        Long id,
        String name,
        String email
) {
}
