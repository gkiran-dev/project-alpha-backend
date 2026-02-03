package com.kiran.alpha.controller;

import com.kiran.alpha.dto.LoginRequest;
import com.kiran.alpha.dto.LoginResponse;
import com.kiran.alpha.dto.LogoutRequest;
import com.kiran.alpha.dto.RefreshTokenRequest;
import com.kiran.alpha.dto.RegisterRequest;
import com.kiran.alpha.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody RefreshTokenRequest request) {
        String newAccessToken =
                authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

}
