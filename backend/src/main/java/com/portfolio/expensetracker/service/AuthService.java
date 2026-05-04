package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.model.RefreshToken;
import com.portfolio.expensetracker.model.User;
import com.portfolio.expensetracker.model.Category;
import com.portfolio.expensetracker.dto.AuthResponse;
import com.portfolio.expensetracker.dto.LoginRequest;
import com.portfolio.expensetracker.dto.RefreshRequest;
import com.portfolio.expensetracker.dto.RegisterRequest;
import com.portfolio.expensetracker.dto.UserProfileResponse;
import com.portfolio.expensetracker.common.exception.ApiException;
import com.portfolio.expensetracker.repository.CategoryRepository;
import com.portfolio.expensetracker.repository.RefreshTokenRepository;
import com.portfolio.expensetracker.repository.UserRepository;
import com.portfolio.expensetracker.security.JwtService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = Logger.getLogger(AuthService.class.getName());
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, CategoryRepository categoryRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            log.severe(String.format("AuthService.register(%s) Email already in use", request.email()));
            throw new ApiException(HttpStatus.CONFLICT, "Email already in use");
        }
        log.info(String.format("AuthService.register(%s) started registration", request.email()));
        User user = new User();
        user.setName(request.name().strip());
        user.setEmail(request.email().strip().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        createDefaultCategories(user);

        AuthResponse authResponse = issueTokens(user);
        log.info(String.format("AuthService.register(%s) successfully ended registration", request.email()));
        return authResponse;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> {
                    log.severe(String.format("AuthService.login(%s) Invalid credentials", request.email()));
                    return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
                });
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> {
                    log.severe("AuthService.refresh() refresh token not found");
                    return new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token not found");
                });
        if (refreshToken.getExpiresAt().isBefore(java.time.Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            log.severe("AuthService.refresh() refresh token expired");
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
