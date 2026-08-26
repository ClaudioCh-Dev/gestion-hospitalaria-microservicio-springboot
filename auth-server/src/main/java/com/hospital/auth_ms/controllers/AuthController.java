package com.hospital.auth_ms.controllers;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.auth_ms.dtos.AuthTokenDto;
import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.dtos.TokenDto;
import com.hospital.auth_ms.dtos.UserDto;
import com.hospital.auth_ms.services.AuthService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> jwtCreate(
            @RequestBody UserDto user) {

        AuthTokenDto tokens =
                this.authService.login(user);

        ResponseCookie refreshCookie =
                createRefreshCookie(tokens.getRefreshToken());

        TokenDto response = TokenDto.builder()
                .accessToken(tokens.getAccessToken())
                .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshCookie.toString()
                )
                .body(response);
    }

    @PostMapping("/validate-jwt")
    public ResponseEntity<ClaimsDto> jwtValidate(
            @RequestHeader("access-token") String accessToken) {

        return ResponseEntity.ok(
                this.authService.validateToken(accessToken)
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDto> refreshToken(
            @CookieValue("refresh_token") String refreshToken) {

        AuthTokenDto tokens =
                this.authService.refreshToken(refreshToken);

        ResponseCookie refreshCookie =
                createRefreshCookie(tokens.getRefreshToken());

        TokenDto response = TokenDto.builder()
                .accessToken(tokens.getAccessToken())
                .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshCookie.toString()
                )
                .body(response);
    }

    private ResponseCookie createRefreshCookie(
            String refreshToken) {

        return ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh-token")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}