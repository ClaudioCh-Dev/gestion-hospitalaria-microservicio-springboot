package com.hospital.auth_ms.services;

public interface IEmailService {

    void sendActivationEmail(String email, String token);

}