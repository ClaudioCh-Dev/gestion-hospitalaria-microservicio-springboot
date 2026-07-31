package com.personal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ConfigDebug {

    @Bean
    CommandLineRunner printConfig(Environment env) {
        return args -> {
            System.out.println("===== CONFIG DEBUG =====");
            System.out.println("db.url: " + env.getProperty("db.url"));
            System.out.println("db.username: " + env.getProperty("db.username"));
            System.out.println("spring.datasource.url: " + env.getProperty("spring.datasource.url"));
            System.out.println("profile activo: " + env.getActiveProfiles().length);
            System.out.println("========================");
        };
    }
}