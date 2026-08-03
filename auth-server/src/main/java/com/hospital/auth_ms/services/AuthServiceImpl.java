package com.hospital.auth_ms.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.dtos.TokenDto;
import com.hospital.auth_ms.dtos.UserDto;
import com.hospital.auth_ms.entities.UserEntity;
import com.hospital.auth_ms.exceptions.InvalidCredentialsException;
import com.hospital.auth_ms.exceptions.InvalidTokenException;
import com.hospital.auth_ms.helpers.JwtHelper;
import com.hospital.auth_ms.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;


    @Override
    public TokenDto login(UserDto userDto) {

        final var userFromDb = this.userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException());

        this.validPassword(userDto, userFromDb);

        return TokenDto.builder()
                .accessToken(this.jwtHelper.createToken(ClaimsDto.builder()
                        .userId(userFromDb.getId())
                        .username(userFromDb.getUsername())
                        .role(userFromDb.getRole())
                        .build()))
                .build();
    }


    @Override
    public ClaimsDto validateToken(String accessToken) {

        if (!this.jwtHelper.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        return ClaimsDto.builder()
                .userId(this.jwtHelper.getUserIdFromToken(accessToken))
                .username(this.jwtHelper.getUsernameFromToken(accessToken))
                .role(this.jwtHelper.getRoleFromToken(accessToken))
                .build();
    }


    private void validPassword(UserDto userDto, UserEntity userEntity) {

        if (!this.passwordEncoder.matches(
                userDto.getPassword(),
                userEntity.getPassword()
        )) {
            throw new InvalidCredentialsException();
        }
    }
}