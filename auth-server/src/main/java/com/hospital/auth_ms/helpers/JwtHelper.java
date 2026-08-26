package com.hospital.auth_ms.helpers;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.exceptions.AuthErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import personal.shared.exception.BusinessException;

@Component
@Slf4j
public class JwtHelper {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    @Value("${auth.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    public JwtHelper(
            RSAPrivateKey privateKey,
            RSAPublicKey publicKey) {

        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String createToken(ClaimsDto claims) {

        final var now = new Date();

        final var expirationDate =
                new Date(
                        now.getTime()
                                + accessTokenExpiration * 1000
                );

        return Jwts.builder()
                .subject(claims.getUsername())
                .claim("role", claims.getRole())
                .claim("userId", claims.getUserId())
                .claim("permissions", claims.getPermissions())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {

        Claims claims = parseToken(token);

        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {

        Claims claims = parseToken(token);

        return claims.get("role", String.class);
    }

    public Long getUserIdFromToken(String token) {

        Claims claims = parseToken(token);

        return claims.get("userId", Long.class);
    }

    public Set<String> getPermissionsFromToken(String token) {

        Claims claims = parseToken(token);

        List<?> permissions =
                claims.get("permissions", List.class);

        if (permissions == null) {
            return Set.of();
        }

        return permissions.stream()
                .map(permission -> String.valueOf(permission))
                .collect(Collectors.toSet());
    }

    public boolean isTokenExpired(String token) {

        return getExpirationDate(token)
                .before(new Date());
    }

    public boolean validateToken(String token) {

        try {

            parseToken(token);

            return !isTokenExpired(token);

        } catch (Exception e) {

            log.error(
                    "Invalid JWT token: {}",
                    e.getMessage()
            );

            throw new BusinessException(
                    AuthErrorCode.AUTH_INVALID_TOKEN,
                    "Token inválido"
            );
        }
    }

    private Date getExpirationDate(String token) {

        Claims claims = parseToken(token);

        return claims.getExpiration();
    }

    private Claims parseToken(String token) {

        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}