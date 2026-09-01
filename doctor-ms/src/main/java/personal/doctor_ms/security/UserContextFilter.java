package personal.doctor_ms.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log =
            LoggerFactory.getLogger(UserContextFilter.class);

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

            log.info("========== USER CONTEXT FILTER ==========");
            log.info("Method: {}", request.getMethod());
            log.info("Path: {}", request.getRequestURI());
            log.info("X-User-Id: {}", userId);
            log.info("X-Role: {}", role);
            log.info("X-Permissions: {}", permissionsHeader);

            Set<String> permissions =
                    permissionsHeader == null
                            ? Set.of()
                            : Arrays.stream(
                                    permissionsHeader.split(","))
                                    .map(String::trim)
                                    .collect(Collectors.toSet());

            log.info("Permissions parsed: {}", permissions);

            UserContext context = new UserContext(
                    userId != null
                            ? Long.valueOf(userId)
                            : null,
                    role,
                    permissions
            );

            log.info("UserContext created: userId={}, role={}, permissions={}",
                    userId,
                    role,
                    permissions
            );

            UserContextHolder.set(context);

            log.info("UserContext stored in UserContextHolder");

            filterChain.doFilter(request, response);

        } finally {

            log.info("Clearing UserContextHolder");

            UserContextHolder.clear();
        }
    }
}