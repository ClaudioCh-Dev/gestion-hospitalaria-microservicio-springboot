package com.hospital.auth_ms.services;

import com.hospital.auth_ms.dtos.ClainsDto;
import com.hospital.auth_ms.dtos.TokenDto;
import com.hospital.auth_ms.dtos.UserDto;

public interface AuthService {

    TokenDto login(UserDto userDto);
    ClainsDto validateToken(String accessToken);
}
