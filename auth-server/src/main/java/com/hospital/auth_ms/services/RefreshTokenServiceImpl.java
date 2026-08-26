package com.hospital.auth_ms.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.hospital.auth_ms.exceptions.AuthErrorCode;

import lombok.RequiredArgsConstructor;

import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${auth.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public String createRefreshToken(Long userId) {

        String refreshToken =
                UUID.randomUUID().toString();

        String key =
                "refresh:" + hashToken(refreshToken);

        redisTemplate.opsForValue().set(
                key,
                userId.toString(),
                Duration.ofSeconds(
                        refreshTokenExpiration
                )
        );

        return refreshToken;
    }

    public Long validateRefreshToken(
            String refreshToken) {

        String key =
                "refresh:" + hashToken(refreshToken);

        String userId =
                redisTemplate.opsForValue().get(key);

        if (userId == null) {

            throw new BusinessException(
                    AuthErrorCode.AUTH_INVALID_REFRESH_TOKEN,
                    "Refresh token inválido o expirado"
            );
        }

        return Long.valueOf(userId);
    }

    public void revokeRefreshToken(
            String refreshToken) {

        String key =
                "refresh:" + hashToken(refreshToken);

        redisTemplate.delete(key);
    }

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 no disponible",
                    e
            );
        }
    }
}