package com.hospital.auth_ms.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.hospital.auth_ms.exceptions.AuthErrorCode;

import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl service;

    @BeforeEach
    void configurar() {
        ReflectionTestUtils.setField(service, "from", "no-reply@hospital.com");
        ReflectionTestUtils.setField(service, "frontendUrl", "http://localhost:4200");
    }

    @Test
    void sendActivationEmail_armaElCorreoConElEnlaceDeActivacion() {
        service.sendActivationEmail("ana@mail.com", "tok-123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage mail = captor.getValue();
        assertEquals("ana@mail.com", mail.getTo()[0]);
        assertEquals("no-reply@hospital.com", mail.getFrom());
        assertTrue(mail.getText().contains("http://localhost:4200/activate?token=tok-123"));
    }

    @Test
    void sendActivationEmail_fallaElServidorDeCorreo_lanzaBusinessException() {
        // send() es void: para que lance se usa doThrow(...).when(mock).metodo(...)
        doThrow(new MailSendException("SMTP caído")).when(mailSender).send(any(SimpleMailMessage.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.sendActivationEmail("ana@mail.com", "tok-123"));

        assertEquals(AuthErrorCode.EMAIL_SEND_FAILED.toString(), ex.getCode());
        assertEquals(503, ex.getStatus());
    }
}
