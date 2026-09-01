package personal.billing_ms.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String userId =
                    request.getHeader("X-User-Id");

            String role =
                    request.getHeader("X-Role");

            String permissionsHeader =
                    request.getHeader("X-Permissions");

            Set<String> permissions =
                    permissionsHeader == null
                            ? Set.of()
                            : Arrays.stream(
                                    permissionsHeader.split(","))
                                    .map(String::trim)
                                    .collect(Collectors.toSet());

            UserContext context = new UserContext(
                    userId != null
                            ? Long.valueOf(userId)
                            : null,
                    role,
                    permissions
            );

            UserContextHolder.set(context);

            filterChain.doFilter(request, response);

        } finally {
            UserContextHolder.clear();
        }
    }
}