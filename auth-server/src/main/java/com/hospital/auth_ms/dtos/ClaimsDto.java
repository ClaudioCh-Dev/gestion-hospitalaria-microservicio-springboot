package com.hospital.auth_ms.dtos;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimsDto {

    private Long userId;

    private String username;

    private String role;

    private Set<String> permissions;
}
