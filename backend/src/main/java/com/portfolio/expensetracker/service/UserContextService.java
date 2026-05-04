package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.model.User;
import com.portfolio.expensetracker.exception.ApiException;
import com.portfolio.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserContextService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            log.error("UserContextService.getCurrentUser() unauthorized!");
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> {
                    log.error("UserContextService.getCurrentUser() user not found");
                    return new ApiException(HttpStatus.UNAUTHORIZED, "User not found");
                });
    }
}
