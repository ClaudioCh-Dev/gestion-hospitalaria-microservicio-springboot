package com.hospital.auth_ms.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.hospital.auth_ms.exceptions.AuthErrorCode;
import com.hospital.auth_ms.services.IEmailService;

import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Value("${spring.mail.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendActivationEmail(String email, String token) {

        String activationUrl = frontendUrl + "/activate?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setFrom(from);
        message.setSubject("Activa tu cuenta");

        message.setText(
                "Hola,\n\n" +
                "Tu cuenta ha sido creada correctamente.\n\n" +
                "Para activar tu cuenta, haz clic en el siguiente enlace:\n\n" +
                activationUrl +
                "\n\n" +
                "Este enlace es válido por 24 horas."
        );

        try {
            mailSender.send(message);

        } catch (MailException e) {
            throw new BusinessException(
                    AuthErrorCode.EMAIL_SEND_FAILED,
                    e.getMessage()
            );
        }
    }
}
