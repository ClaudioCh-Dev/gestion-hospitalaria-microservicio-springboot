package com.hospital.auth_ms;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String passwordPlano = "123456";

        String passwordHash = encoder.encode(passwordPlano);

        System.out.println("Password original: " + passwordPlano);
        System.out.println("Password BCrypt: " + passwordHash);
    }
}