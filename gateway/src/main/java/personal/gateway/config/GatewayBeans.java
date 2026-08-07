package personal.gateway.config;

import java.util.Set;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import personal.gateway.filters.AuthFilter;

@Configuration
public class GatewayBeans {

    private final AuthFilter authFilter;

    public GatewayBeans(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    @Profile(value = "eureka-off")
    public RouteLocator routeLocatorEurekaOff(RouteLocatorBuilder builder) {
        return builder.routes()

                .route(route -> route
                        .path("/patient-ms/**")
                        .uri("http://localhost:8081"))

                .route(route -> route
                        .path("/appointment-ms/**")
                        .uri("http://localhost:7080"))

                .route(route -> route
                        .path("/auth-server/**")
                        .uri("http://localhost:3000"))

                .build();
    }

    @Bean
    @Profile(value = "eureka-on")
    public RouteLocator routeLocatorEurekaOn(RouteLocatorBuilder builder) {
        return builder.routes()

                .route(route -> route
                        .path("/patient-ms/**")
                        .uri("lb://patient-ms"))

                .route(route -> route
                        .path("/doctor-ms/**")
                        .uri("lb://doctor-ms"))

                .route(route -> route
                        .path("/appointment-ms/**")
                        .uri("lb://appointment-ms"))

                .route(route -> route
                        .path("/billing-ms/**")
                        .uri("lb://billing-ms"))

                .route(route -> route
                        .path("/auth-server/**")
                        .uri("lb://auth-server"))

                .build();
    }

    @Bean
    @Profile(value = "eureka-on-cb")
    public RouteLocator routeLocatorEurekaOnCB(RouteLocatorBuilder builder) {
        return builder.routes()

                .route(route -> route
                        .path("/patient-ms/**")
                        .filters(filter -> filter.circuitBreaker(
                                config -> config
                                        .setName("patient-circuitbreaker")
                                        .setStatusCodes(Set.of("500"))
                                        .setFallbackUri("forward:/patient-ms-fallback/**")))
                        .uri("lb://patient-ms"))

                .route(route -> route
                        .path("/patient-ms-fallback/**")
                        .uri("lb://patient-ms-fallback"))

                .route(route -> route
                        .path("/appointment-ms/**")
                        .uri("lb://appointment-ms"))

                .route(route -> route
                        .path("/doctor-ms/**")
                        .uri("lb://doctor-ms"))

                .route(route -> route
                        .path("/billing-ms/**")
                        .uri("lb://billing-ms"))

                .route(route -> route
                        .path("/auth-server/**")
                        .uri("lb://auth-server"))

                .build();
    }

    @Bean
    @Profile(value = "oauth2")
    public RouteLocator routeLocatorOAuth2(RouteLocatorBuilder builder) {

        System.out.println("========== PERFIL OAUTH2 ACTIVO ==========");

        return builder.routes()

                /*
                 * .route(route -> route
                 * .path("/patient-ms/**")
                 * .filters(filter -> filter
                 * .circuitBreaker(config -> config
                 * .setName("patient-circuitbreaker")
                 * .setStatusCodes(Set.of("500"))
                 * .setFallbackUri("forward:/patient-ms-fallback/**")
                 * )
                 * .filter(this.authFilter)
                 * )
                 * .uri("lb://patient-ms")
                 * )
                 */

                
                .route(route -> route
                        .path("/patient-ms/**")
                        .filters(filter -> filter
                                .filter((exchange, chain) -> {
                                    System.out.println("=========== FILTRO DE RUTA ENTRÓ ===========");
                                    return chain.filter(exchange);
                                })
                                .filter(this.authFilter))
                        .uri("lb://patient-ms"))
                /*
                 * /
                 * .route(route -> route
                 * .path("/doctor-ms/**")
                 * .filters(filter -> filter.filter(this.authFilter))
                 * .uri("lb://doctor-ms")
                 * )
                 * 
                 * .route(route -> route
                 * .path("/appointment-ms/**")
                 * .filters(filter -> filter.filter(this.authFilter))
                 * .uri("lb://appointment-ms")
                 * )
                 * 
                 * .route(route -> route
                 * .path("/billing-ms/**")
                 * .filters(filter -> filter.filter(this.authFilter))
                 * .uri("lb://billing-ms")
                 * )
                 * 
                 * .route(route -> route
                 * .path("/patient-ms-fallback/**")
                 * .uri("lb://patient-ms-fallback")
                 * )
                 * 
                 * .route(route -> route
                 * .path("/auth-server/**")
                 * .uri("lb://auth-server")
                 * )
                 */
                .build();
    }
}