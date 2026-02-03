package com.kiran.alpha.repository;

import com.kiran.alpha.entity.RefreshToken;
import com.kiran.alpha.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
