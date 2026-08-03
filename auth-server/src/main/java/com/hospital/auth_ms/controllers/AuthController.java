package com.hospital.auth_ms.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.dtos.UserDto;
import com.hospital.auth_ms.dtos.TokenDto;
import com.hospital.auth_ms.services.AuthService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> jwtCreate(@RequestBody UserDto user) {
        return ResponseEntity.ok(this.authService.login(user));
    }

    @PostMapping("/validate-jwt")
    public ResponseEntity<ClaimsDto> jwtValidate(
            @RequestHeader(value = "access-token") String accessToken) {
        return ResponseEntity.ok(this.authService.validateToken(accessToken));
    }
}