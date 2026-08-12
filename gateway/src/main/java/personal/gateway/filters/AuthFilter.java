package personal.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements WebFilter {

    private static final Logger log =
            LoggerFactory.getLogger(AuthFilter.class);

    private static final String USER_ID_HEADER =
            "X-User-Id";

    private static final String ROLE_HEADER =
            "X-Role";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain
    ) {

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {

                    /*
                     * Spring Security ya validó el JWT.
                     */
                    if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {

                        log.warn(
                                "Authentication no es JwtAuthenticationToken"
                        );

                        return chain.filter(exchange);
                    }

                    var jwt = jwtAuth.getToken();

                    /*
                     * Claims obtenidos del JWT VALIDADO.
                     */
                    String userId =
                            jwt.getClaimAsString("userId");

                    String username =
                            jwt.getSubject();

                    String role =
                            jwt.getClaimAsString("role");

                    log.info("=================================");
                    log.info("JWT autenticado correctamente");
                    log.info("UserId: {}", userId);
                    log.info("Username: {}", username);
                    log.info("Role: {}", role);

                    /*
                     * Eliminamos headers enviados por el cliente.
                     */
                    ServerWebExchange mutatedExchange =
                            exchange.mutate()
                                    .request(request ->
                                            request.headers(headers -> {

                                                headers.remove(
                                                        USER_ID_HEADER
                                                );

                                                headers.remove(
                                                        ROLE_HEADER
                                                );

                                                /*
                                                 * Colocamos nuestros
                                                 * valores confiables.
                                                 */
                                                if (userId != null) {
                                                    headers.set(
                                                            USER_ID_HEADER,
                                                            userId
                                                    );
                                                }

                                                if (role != null) {
                                                    headers.set(
                                                            ROLE_HEADER,
                                                            role
                                                    );
                                                }
                                            })
                                    )
                                    .build();

                    return chain.filter(mutatedExchange);
                });
    }
}