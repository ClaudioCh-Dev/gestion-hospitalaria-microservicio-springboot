package com.hospital.auth_ms.helpers;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Construye la cookie del refresh token.
 * Secure / SameSite / Path salen de auth.refresh-token.cookie.* para poder
 * relajarlos por perfil (dev: http, prod: https) desde el Config Server.
 */
@Component
public class RefreshCookieHelper {

    public static final String COOKIE_NAME = "refresh_token";

    @Value("${auth.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Value("${auth.refresh-token.cookie.secure:true}")
    private boolean secure;

    @Value("${auth.refresh-token.cookie.same-site:Strict}")
    private String sameSite;

    @Value("${auth.refresh-token.cookie.path:/auth-server/auth/refresh-token}")
    private String path;

    public ResponseCookie create(String refreshToken) {
        return build(refreshToken, Duration.ofSeconds(refreshTokenExpiration));
    }

    // Para borrarla el navegador exige el mismo name + path que la original
    public ResponseCookie clear() {
        return build("", Duration.ZERO);
    }

    private ResponseCookie build(String value, Duration maxAge) {
        return ResponseCookie
                .from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(maxAge)
                .build();
    }
}
