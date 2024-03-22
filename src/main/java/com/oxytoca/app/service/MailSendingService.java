package com.oxytoca.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки сообщений по SMTP-протоколу
 *
 */

@Service
public class MailSendingService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public MailSendingService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Метод сервиса для отправки email-сообщений.
     * @param recipient - email получателя сообщения.
     * @param subject - параметр для установки темы электронного письма.
     * @param message - текст электронного письма.
     */
    public void sendMail(String recipient, String subject,
                         String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(recipient);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

}
