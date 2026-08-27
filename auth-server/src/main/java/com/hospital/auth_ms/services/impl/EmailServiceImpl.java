package com.hospital.auth_ms.services.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.hospital.auth_ms.services.IEmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendActivationEmail(String email, String token) {

        String activationUrl =
                "http://localhost:4200/activate?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Activa tu cuenta");
        message.setText(
                "Hola,\n\n" +
                "Tu cuenta ha sido creada correctamente.\n\n" +
                "Para activar tu cuenta, haz clic en el siguiente enlace:\n\n" +
                activationUrl +
                "\n\n" +
                "Este enlace es válido por 24 horas."
        );

        mailSender.send(message);
    }
}