package personal.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import personal.gateway.dto.UserInfoDto;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GatewayFilter {


    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);


    private final WebClient webClient;

    private final String authValidateUri;


    private static final String ACCESS_TOKEN_HEADER = "access-token";

    private static final String USER_ID_HEADER = "X-User-Id";

    private static final String USERNAME_HEADER = "X-Username";

    private static final String ROLE_HEADER = "X-Role";


    public AuthFilter(
            WebClient.Builder webClientBuilder,
            @Value("${gateway.auth.validate-uri:http://localhost:3000/auth-server/auth/validate-jwt}")
            String authValidateUri
    ) {

        this.webClient = webClientBuilder.build();
        this.authValidateUri = authValidateUri;

        log.info("AuthFilter iniciado");
        log.info("Auth validate URI: {}", authValidateUri);

        System.out.println("######## AUTH FILTER CONSTRUCTOR ########");
        System.out.println("Auth validate URI: " + authValidateUri);
    }


    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {


        String path = exchange.getRequest()
                .getURI()
                .getPath();


        log.info("=================================");
        log.info("AuthFilter ejecutando");
        log.info("Request path: {}", path);


        String tokenHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");


        log.info("Authorization header existe: {}", tokenHeader != null);


        if (tokenHeader == null) {

            log.error("No existe header Authorization");

            return onError(exchange);
        }


        log.info("Authorization recibido: {}",
                tokenHeader.substring(0, Math.min(tokenHeader.length(), 20))
        );


        String[] chunks = tokenHeader.split(" ");


        if (chunks.length != 2 || !"Bearer".equals(chunks[0])) {

            log.error("Formato Authorization incorrecto");

            return onError(exchange);
        }


        String token = chunks[1];

        log.info("Token extraído: {}", token);


        log.info("Token recibido longitud: {}", token.length());


        log.info("Validando token contra auth-server: {}", authValidateUri);


        return webClient.post()

                .uri(authValidateUri)

                // manda JWT al auth-ms
                .header(ACCESS_TOKEN_HEADER, token)

                .retrieve()


                .bodyToMono(UserInfoDto.class)


                .doOnNext(user -> {

                    log.info("Token válido");
                    log.info("Usuario autenticado:");
                    log.info("UserId: {}", user.getUserId());
                    log.info("Username: {}", user.getUsername());
                    log.info("Role: {}", user.getRole());

                })


                .map(user -> exchange.mutate()

                        .request(request -> request

                                .header(
                                        USER_ID_HEADER,
                                        user.getUserId().toString()
                                )

                                .header(
                                        USERNAME_HEADER,
                                        user.getUsername()
                                )

                                .header(
                                        ROLE_HEADER,
                                        user.getRole()
                                )

                        )

                        .build()

                )


                .flatMap(chain::filter)


                .doOnError(error -> {

                    log.error("Error validando JWT");
                    log.error("Tipo: {}", error.getClass().getName());
                    log.error("Mensaje: {}", error.getMessage());

                })

                .onErrorResume(error -> {

                    return onError(exchange);

                });

    }



    private Mono<Void> onError(ServerWebExchange exchange) {


        log.error("Acceso rechazado -> 401");


        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);


        return exchange.getResponse()
                .setComplete();

    }

}