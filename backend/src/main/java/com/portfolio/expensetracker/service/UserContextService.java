package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.model.User;
import com.portfolio.expensetracker.common.exception.ApiException;
import com.portfolio.expensetracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserContextService {
    private static final Logger log = Logger.getLogger(UserContextService.class.getName());
    private final UserRepository userRepository;

    public UserContextService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            log.severe("UserContextService.getCurrentUser() unauthorized!");
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> {
                    log.severe("UserContextService.getCurrentUser() user not found");
                    return new ApiException(HttpStatus.UNAUTHORIZED, "User not found");
                });
    }
}
