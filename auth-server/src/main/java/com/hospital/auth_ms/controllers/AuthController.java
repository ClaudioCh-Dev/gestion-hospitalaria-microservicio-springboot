package com.hospital.auth_ms.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.auth_ms.dtos.authentication.AuthTokenDto;
import com.hospital.auth_ms.dtos.authentication.ClaimsDto;
import com.hospital.auth_ms.dtos.authentication.TokenDto;
import com.hospital.auth_ms.dtos.authentication.UserDto;
import com.hospital.auth_ms.helpers.RefreshCookieHelper;
import com.hospital.auth_ms.services.IAuthService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final RefreshCookieHelper refreshCookieHelper;

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<TokenDto> jwtCreate(
            @RequestBody UserDto user) {

        AuthTokenDto tokens =
                this.authService.login(user);

        ResponseCookie refreshCookie =
                refreshCookieHelper.create(tokens.getRefreshToken());

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

    // =========================
    // VALIDATE JWT
    // =========================

    @PostMapping("/validate-jwt")
    public ResponseEntity<ClaimsDto> jwtValidate(
            @RequestHeader("access-token") String accessToken) {

        return ResponseEntity.ok(
                this.authService.validateToken(accessToken)
        );
    }

    // =========================
    // REFRESH TOKEN
    // =========================

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDto> refreshToken(
            @CookieValue(RefreshCookieHelper.COOKIE_NAME) String refreshToken) {

        AuthTokenDto tokens =
                this.authService.refreshToken(refreshToken);

        ResponseCookie refreshCookie =
                refreshCookieHelper.create(tokens.getRefreshToken());

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

    // =========================
    // LOGOUT
    // =========================

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie cookie = refreshCookieHelper.clear();

        return ResponseEntity.noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .build();
    }
}
