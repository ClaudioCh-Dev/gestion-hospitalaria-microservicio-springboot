package com.hospital.auth_ms.helpers;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.exceptions.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtHelper {

    @Value("${application.jwt.secret}")
    private String jwtSecret;

    public String createToken(ClaimsDto claims) {
        final var now = new Date();
        final var expirationDate = new Date(now.getTime() + 3600 * 1000);

        return Jwts.builder()
                .subject(claims.getUsername())
                .claim("role", claims.getRole())
                .claim("userId", claims.getUserId())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token, claims -> claims.getSubject());
    }

    public String getRoleFromToken(String token) {
        return getClaimsFromToken(
                token,
                claims -> claims.get("role", String.class)
        );
    }

    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(
                token,
                claims -> claims.get("userId", Long.class)
        );
    }

    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException();
        }
    }

    private Date getExpirationDate(String token) {
        return getClaimsFromToken(token, claims -> claims.getExpiration());
    }

    private <T> T getClaimsFromToken(
            String token,
            Function<Claims, T> resolver
    ) {
        return resolver.apply(parseToken(token));
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }
}
