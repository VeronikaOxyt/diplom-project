package com.oxytoca.app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailSendingService mailSendingService;

    @Test
    void testSendMail() {
        ReflectionTestUtils.setField(mailSendingService, "username", "oxytoca@example.com");

        mailSendingService.sendMail("recipient@example.com", "Test Subject", "Test Message");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
