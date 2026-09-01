package com.hospital.auth_ms.services.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.hospital.auth_ms.exceptions.AuthErrorCode;
import com.hospital.auth_ms.services.IRefreshTokenService;

import lombok.RequiredArgsConstructor;

import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${auth.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Override
    public String createRefreshToken(Long userId) {

        String refreshToken = UUID.randomUUID().toString();

        String tokenHash = hashToken(refreshToken);

        String key = "refresh:" + tokenHash;

        redisTemplate.opsForValue().set(
                key,
                userId.toString(),
                Duration.ofSeconds(refreshTokenExpiration)
        );

        // Índice de tokens pertenecientes al usuario
        String userKey = "user-refresh:" + userId;

        redisTemplate.opsForSet().add(
                userKey,
                tokenHash
        );

        return refreshToken;
    }

    @Override
    public Long validateRefreshToken(String refreshToken) {

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

    @Override
    public void revokeRefreshToken(String refreshToken) {

        String tokenHash = hashToken(refreshToken);

        String key = "refresh:" + tokenHash;

        String userId =
                redisTemplate.opsForValue().get(key);

        if (userId != null) {

            redisTemplate.opsForSet().remove(
                    "user-refresh:" + userId,
                    tokenHash
            );
        }

        redisTemplate.delete(key);
    }

    @Override
    public void revokeAllByUserId(Long userId) {

        String userKey = "user-refresh:" + userId;

        Set<String> tokenHashes =
                redisTemplate.opsForSet().members(userKey);

        if (tokenHashes == null || tokenHashes.isEmpty()) {
            return;
        }

        for (String tokenHash : tokenHashes) {

            redisTemplate.delete(
                    "refresh:" + tokenHash
            );
        }

        redisTemplate.delete(userKey);
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