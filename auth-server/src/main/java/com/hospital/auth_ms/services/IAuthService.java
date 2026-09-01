package com.hospital.auth_ms.services;

import com.hospital.auth_ms.dtos.authentication.AuthTokenDto;
import com.hospital.auth_ms.dtos.authentication.ClaimsDto;
import com.hospital.auth_ms.dtos.authentication.UserDto;

public interface IAuthService {

    AuthTokenDto login(UserDto userDto);

    ClaimsDto validateToken(String accessToken);

    AuthTokenDto refreshToken(String refreshToken);

}