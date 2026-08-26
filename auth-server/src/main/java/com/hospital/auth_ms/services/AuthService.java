package com.hospital.auth_ms.services;

import com.hospital.auth_ms.dtos.AuthTokenDto;
import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.dtos.UserDto;

public interface AuthService {

    AuthTokenDto login(UserDto userDto);

    ClaimsDto validateToken(String accessToken);

    AuthTokenDto refreshToken(String refreshToken);

}