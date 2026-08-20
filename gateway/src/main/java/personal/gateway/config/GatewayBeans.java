package personal.gateway.config;

import java.util.Set;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class GatewayBeans {

        // ============================================================
        // EUREKA OFF
        // ============================================================

        @Bean
        @Profile("eureka-off")
        public RouteLocator routeLocatorEurekaOff(
                        RouteLocatorBuilder builder) {

                return builder.routes()

                                .route(route -> route
                                                .path("/patient-ms/**")
                                                .uri("http://localhost:8081"))

                                .route(route -> route
                                                .path("/doctor-ms/**")
                                                .uri("http://localhost:8082"))

                                .route(route -> route
                                                .path("/appointment-ms/**")
                                                .uri("http://localhost:8083"))

                                .route(route -> route
                                                .path("/billing-ms/**")
                                                .uri("http://localhost:8084"))

                                .route(route -> route
                                                .path("/auth-server/**")
                                                .uri("http://localhost:3000"))

                                .build();
        }

        // ============================================================
        // EUREKA ON
        // ============================================================

        @Bean
        @Profile("eureka-on")
        public RouteLocator routeLocatorEurekaOn(
                        RouteLocatorBuilder builder) {

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

        // ============================================================
        // EUREKA ON + CIRCUIT BREAKER
        // ============================================================

        @Bean
        @Profile("eureka-on-cb")
        public RouteLocator routeLocatorEurekaOnCB(
                        RouteLocatorBuilder builder) {

                return builder.routes()

                                .route(route -> route
                                                .path("/patient-ms/**")
                                                .filters(filter -> filter
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "patient-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback?service=patient-ms")))
                                                .uri("lb://patient-ms"))

                                .route(route -> route
                                                .path("/doctor-ms/**")
                                                .filters(filter -> filter
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "doctor-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback?service=doctor-ms")))
                                                .uri("lb://doctor-ms"))

                                .route(route -> route
                                                .path("/appointment-ms/**")
                                                .filters(filter -> filter
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "appointment-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback?service=appointment-ms")))
                                                .uri("lb://appointment-ms"))

                                .route(route -> route
                                                .path("/billing-ms/**")
                                                .filters(filter -> filter
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "billing-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback?service=billing-ms")))
                                                .uri("lb://billing-ms"))

                                .route(route -> route
                                                .path("/auth-server/**")
                                                .uri("lb://auth-server"))

                                .build();
        }

        // ============================================================
        // OAUTH2
        // ============================================================

        @Bean
        @Profile("oauth2")
        public RouteLocator routeLocatorOAuth2(
                        RouteLocatorBuilder builder) {

                System.out.println(
                                "========== PERFIL OAUTH2 ACTIVO ==========");

                return builder.routes()

                                // ------------------------------------------------
                                // PATIENT
                                // ------------------------------------------------

                                .route(route -> route
                                                .path("/patients/**")
                                                .filters(filter -> filter
                                                                .addRequestHeader(
                                                                                "X-Fallback-Service",
                                                                                "patient-ms")
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "patient-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback")))
                                                .uri("lb://patient-ms"))

                                // ------------------------------------------------
                                // DOCTOR
                                // ------------------------------------------------

                                .route(route -> route
                                                .path("/doctors/**")
                                                .filters(filter -> filter
                                                                .addRequestHeader(
                                                                                "X-Fallback-Service",
                                                                                "doctor-ms")
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "doctor-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback")))
                                                .uri("lb://doctor-ms"))

                                // ------------------------------------------------
                                // APPOINTMENT
                                // ------------------------------------------------

                                .route(route -> route
                                                .path("/appointments/**")
                                                .filters(filter -> filter
                                                                .addRequestHeader(
                                                                                "X-Fallback-Service",
                                                                                "appointment-ms")
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "appointment-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback")))
                                                .uri("lb://appointment-ms"))

                                // ------------------------------------------------
                                // BILLING
                                // ------------------------------------------------

                                .route(route -> route
                                                .path("/billings/**")
                                                .filters(filter -> filter
                                                                .addRequestHeader(
                                                                                "X-Fallback-Service",
                                                                                "billing-ms")
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "billing-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback")))
                                                .uri("lb://billing-ms"))

                                // ------------------------------------------------
                                // NOTIFICATION
                                // ------------------------------------------------

                                .route(route -> route
                                                .path("/notifications/**")
                                                .filters(filter -> filter
                                                                .addRequestHeader(
                                                                                "X-Fallback-Service",
                                                                                "notification-ms")
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "notification-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback")))
                                                .uri("lb://notification-ms"))

                                // ------------------------------------------------
                                // MEDICAL RECORD LISTENER
                                // ------------------------------------------------

                                .route(route -> route
                                                .path("/medical-record-listener/**")
                                                .filters(filter -> filter
                                                                .addRequestHeader(
                                                                                "X-Fallback-Service",
                                                                                "medical-record-listener-ms")
                                                                .circuitBreaker(config -> config
                                                                                .setName(
                                                                                                "medical-record-listener-circuitbreaker")
                                                                                .setStatusCodes(
                                                                                                Set.of("500"))
                                                                                .setFallbackUri(
                                                                                                "forward:/fallback")))
                                                .uri("lb://medical-record-listener-ms"))

                                .build();
        }
}