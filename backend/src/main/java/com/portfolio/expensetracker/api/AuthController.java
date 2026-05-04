package com.portfolio.expensetracker.api;

import com.portfolio.expensetracker.common.statics.Endpoints;
import com.portfolio.expensetracker.dto.AuthResponse;
import com.portfolio.expensetracker.dto.LoginRequest;
import com.portfolio.expensetracker.dto.RefreshRequest;
import com.portfolio.expensetracker.dto.RegisterRequest;
import com.portfolio.expensetracker.service.AuthService;
import com.portfolio.expensetracker.service.UserContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AUTH_V1_API)
@Tag(name = "Authorization API")
public class AuthController {

    private final AuthService authService;
    private final UserContextService userContextService;

    public AuthController(AuthService authService, UserContextService userContextService) {
        this.authService = authService;
        this.userContextService = userContextService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registration", description = "Registration in system", operationId = "register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login in system", operationId = "login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh", description = "Retrieve refresh token", operationId = "refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout from system", operationId = "logout")
    public ResponseEntity<Void> logout() {
        authService.logout(userContextService.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}
