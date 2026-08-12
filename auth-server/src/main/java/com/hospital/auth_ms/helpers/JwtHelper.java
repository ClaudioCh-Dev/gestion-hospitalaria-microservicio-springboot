package com.hospital.auth_ms.helpers;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.exceptions.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtHelper {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public JwtHelper(
            RSAPrivateKey privateKey,
            RSAPublicKey publicKey
    ) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String createToken(ClaimsDto claims) {

        final var now = new Date();

        final var expirationDate =
                new Date(now.getTime() + 3600 * 1000);

        return Jwts.builder()
                .subject(claims.getUsername())
                .claim("role", claims.getRole())
                .claim("userId", claims.getUserId())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {

        return getClaimsFromToken(
                token,
                Claims::getSubject
        );
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

            throw new InvalidTokenException();
        }
    }

    private Date getExpirationDate(String token) {

        return getClaimsFromToken(
                token,
                Claims::getExpiration
        );
    }

    private <T> T getClaimsFromToken(
            String token,
            Function<Claims, T> resolver
    ) {

        return resolver.apply(
                parseToken(token)
        );
    }

    private Claims parseToken(String token) {

        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}