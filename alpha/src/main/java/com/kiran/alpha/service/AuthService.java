package com.kiran.alpha.service;

import com.kiran.alpha.dto.LoginRequest;
import com.kiran.alpha.dto.LoginResponse;
import com.kiran.alpha.dto.RegisterRequest;
import com.kiran.alpha.entity.RefreshToken;
import com.kiran.alpha.entity.User;
import com.kiran.alpha.repository.RefreshTokenRepository;
import com.kiran.alpha.repository.UserRepository;
import com.kiran.alpha.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        refreshTokenRepository.deleteByUser(user);

        // Generate access token (JWT)
        String accessToken = jwtUtil.generateToken(user.getEmail());

        // Generate refresh token
        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, refreshTokenValue);
    }

    public String refreshAccessToken(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        return jwtUtil.generateToken(user.getEmail());
    }

    public void logout(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenRepository.delete(refreshToken);
    }


}
