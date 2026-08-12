package com.hospital.auth_ms.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RsaKeyConfig {

    @Bean
    public RSAPrivateCrtKey privateKey() throws Exception {

        String pem = Files.readString(
                Path.of(
                        "src/main/resources/keys/private-key-pkcs8.pem"
                )
        );

        String key = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(decoded);

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        return (RSAPrivateCrtKey)
                keyFactory.generatePrivate(spec);
    }

    @Bean
    public RSAPublicKey publicKey(
            RSAPrivateCrtKey privateKey
    ) throws Exception {

        RSAPublicKeySpec publicKeySpec =
                new RSAPublicKeySpec(
                        privateKey.getModulus(),
                        privateKey.getPublicExponent()
                );

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        return (RSAPublicKey)
                keyFactory.generatePublic(publicKeySpec);
    }
}