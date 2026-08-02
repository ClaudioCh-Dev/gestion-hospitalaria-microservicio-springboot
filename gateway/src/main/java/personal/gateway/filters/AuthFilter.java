package personal.gateway.filters;

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


    private final WebClient webClient;

    private final String authValidateUri;


    private static final String ACCESS_TOKEN_HEADER = "accessToken";

    private static final String USER_ID_HEADER = "X-User-Id";

    private static final String USERNAME_HEADER = "X-Username";

    private static final String ROLE_HEADER = "X-Role";


    public AuthFilter(
            WebClient.Builder webClientBuilder,
            @Value("${gateway.auth.validate-uri:http://localhost:3000/auth-server/auth/jwt}")
            String authValidateUri
    ) {
        this.webClient = webClientBuilder.build();
        this.authValidateUri = authValidateUri;
    }


    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {


        String tokenHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");


        if (tokenHeader == null) {
            return onError(exchange);
        }


        String[] chunks = tokenHeader.split(" ");


        if (chunks.length != 2 || !"Bearer".equals(chunks[0])) {
            return onError(exchange);
        }


        String token = chunks[1];


        return webClient.post()
                .uri(authValidateUri)

                // manda JWT al auth-ms
                .header(ACCESS_TOKEN_HEADER, token)

                .retrieve()

                // recibe userId, username, role
                .bodyToMono(UserInfoDto.class)

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

                .flatMap(chain::filter);
    }


    private Mono<Void> onError(ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse()
                .setComplete();
    }
}