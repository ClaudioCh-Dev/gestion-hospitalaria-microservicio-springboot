package com.hospital.auth_ms.services;

import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.dtos.TokenDto;
import com.hospital.auth_ms.dtos.UserDto;

public interface AuthService {

    TokenDto login(UserDto userDto);
    ClaimsDto validateToken(String accessToken);
}
