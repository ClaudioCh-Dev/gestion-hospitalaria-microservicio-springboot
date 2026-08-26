package com.hospital.auth_ms.services;;

public interface RefreshTokenService {

    String createRefreshToken(Long userId);

    Long validateRefreshToken(String refreshToken);

    void revokeRefreshToken(String refreshToken);

}