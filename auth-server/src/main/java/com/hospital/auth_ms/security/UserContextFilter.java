package com.hospital.auth_ms.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {

                var jwt = jwtAuth.getToken();

                Long userId = jwt.getClaim("userId");

                String role = jwt.getClaim("role");

                var permissionsClaim =
                        jwt.getClaimAsStringList("permissions");

                Set<String> permissions =
                        permissionsClaim != null
                                ? Set.copyOf(permissionsClaim)
                                : Collections.emptySet();

                UserContext context = new UserContext(
                        userId,
                        role,
                        permissions
                );

                UserContextHolder.set(context);
            }

            filterChain.doFilter(request, response);

        } finally {

            UserContextHolder.clear();
        }
    }
}