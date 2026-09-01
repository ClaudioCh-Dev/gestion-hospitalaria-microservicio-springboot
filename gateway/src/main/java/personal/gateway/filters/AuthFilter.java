package personal.gateway.filters;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;

// TODO Implementar autenticación **service-to-service con OAuth2 Client Credentials** entre `doctor-ms` y `auth-server`, utilizando un **Service Token independiente del JWT del usuario**, con scopes específicos para proteger los endpoints internos.

@Component
public class AuthFilter implements WebFilter {

        private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

        private static final String USER_ID_HEADER = "X-User-Id";
        private static final String ROLE_HEADER = "X-Role";
        private static final String PERMISSIONS_HEADER = "X-Permissions";

        @Override
        public Mono<Void> filter(
                        ServerWebExchange exchange,
                        WebFilterChain chain) {

                log.info(
                                "🔥 AUTH FILTER: {}",
                                exchange.getRequest().getPath());

                return exchange.getPrincipal()
                                .cast(Authentication.class)

                                .flatMap(authentication -> {

                                        log.info(
                                                        "🔥 AUTHENTICATION: {}",
                                                        authentication);

                                        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {

                                                log.warn("Authentication no es JwtAuthenticationToken");

                                                return chain.filter(exchange);
                                        }

                                        var jwt = jwtAuth.getToken();

                                        String userId = jwt.getClaimAsString("userId");

                                        String username = jwt.getSubject();

                                        String role = jwt.getClaimAsString("role");

                                        List<String> permissions = jwt.getClaimAsStringList("permissions");

                                        log.info("=================================");
                                        log.info("JWT autenticado correctamente");
                                        log.info("UserId: {}", userId);
                                        log.info("Username: {}", username);
                                        log.info("Role: {}", role);
                                        log.info("Permissions: {}", permissions);

                                        String authorization = exchange.getRequest()
                                                        .getHeaders()
                                                        .getFirst(HttpHeaders.AUTHORIZATION);

                                        ServerWebExchange mutatedExchange = exchange.mutate()
                                                        .request(request -> request.headers(headers -> {

                                                                headers.remove(USER_ID_HEADER);
                                                                headers.remove(ROLE_HEADER);
                                                                headers.remove(PERMISSIONS_HEADER);

                                                                if (authorization != null) {
                                                                        headers.set(HttpHeaders.AUTHORIZATION,
                                                                                        authorization);
                                                                }

                                                                if (userId != null) {
                                                                        headers.set(USER_ID_HEADER, userId);
                                                                }

                                                                if (role != null) {
                                                                        headers.set(ROLE_HEADER, role);
                                                                }

                                                                if (permissions != null) {
                                                                        headers.set(PERMISSIONS_HEADER,
                                                                                        String.join(",", permissions));
                                                                }

                                                        }))
                                                        .build();
                                        return chain.filter(mutatedExchange);
                                })
                                .switchIfEmpty(
                                                // NO hay JWT → dejamos que Spring Security
                                                // se encargue de rechazar la petición
                                                chain.filter(exchange));
        }
}