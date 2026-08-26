package com.hospital.auth_ms.services;

public interface IRefreshTokenService {

    String createRefreshToken(Long userId);

    Long validateRefreshToken(String refreshToken);

    void revokeRefreshToken(String refreshToken);

    void revokeAllByUserId(Long userId);
}