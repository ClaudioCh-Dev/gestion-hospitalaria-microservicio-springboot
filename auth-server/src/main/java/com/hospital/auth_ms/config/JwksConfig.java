package com.hospital.auth_ms.config;

import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

@Configuration
public class JwksConfig {

    @Bean
    public JWKSet jwkSet(
            RSAPublicKey publicKey
    ) {

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .keyID("hospital-auth-key")
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();

        return new JWKSet(rsaKey);
    }
}