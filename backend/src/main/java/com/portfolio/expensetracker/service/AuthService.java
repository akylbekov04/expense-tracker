package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.model.RefreshToken;
import com.portfolio.expensetracker.model.User;
import com.portfolio.expensetracker.model.Category;
import com.portfolio.expensetracker.dto.AuthResponse;
import com.portfolio.expensetracker.dto.LoginRequest;
import com.portfolio.expensetracker.dto.RefreshRequest;
import com.portfolio.expensetracker.dto.RegisterRequest;
import com.portfolio.expensetracker.dto.UserProfileResponse;
import com.portfolio.expensetracker.exception.ApiException;
import com.portfolio.expensetracker.repository.CategoryRepository;
import com.portfolio.expensetracker.repository.RefreshTokenRepository;
import com.portfolio.expensetracker.repository.UserRepository;
import com.portfolio.expensetracker.security.JwtService;
import jakarta.transaction.Transactional;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            log.error("AuthService.register({}) Email already in use", request.email());
            throw new ApiException(HttpStatus.CONFLICT, "Email already in use");
        }
        log.info("AuthService.register({}) started registration", request.email());
        User user = new User();
        user.setName(request.name().strip());
        user.setEmail(request.email().strip().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        createDefaultCategories(user);

        AuthResponse authResponse = issueTokens(user);
        log.info("AuthService.register({}) successfully ended registration", request.email());
        return authResponse;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> {
                    log.error("AuthService.login({}) Invalid credentials", request.email());
                    return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
                });
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> {
                    log.error("AuthService.refresh() refresh token not found");
                    return new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token not found");
                });
        if (refreshToken.getExpiresAt().isBefore(java.time.Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            log.error("AuthService.refresh() refresh token expired");
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        refreshTokenRepository.delete(refreshToken);
        return issueTokens(refreshToken.getUser());
    }

    @Transactional
    public void logout(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiresAt(jwtService.extractExpiration(refreshTokenValue));
        refreshToken.setUser(user);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenValue,
                "Bearer",
                new UserProfileResponse(user.getId(), user.getName(), user.getEmail())
        );
    }

    private void createDefaultCategories(User user) {
        List<Category> categories = List.of(
                buildCategory(user, "Food", "#ef476f"),
                buildCategory(user, "Transport", "#118ab2"),
                buildCategory(user, "Bills", "#073b4c"),
                buildCategory(user, "Shopping", "#f78c6b")
        );
        categoryRepository.saveAll(categories);
    }

    private Category buildCategory(User user, String name, String color) {
        Category category = new Category();
        category.setUser(user);
        category.setName(name);
        category.setColor(color);
        return category;
    }
}
