package ru.royal.fileExchanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender){
        this.mailSender =mailSender;
    }

    /**
     * Отправляет email сообщение.
     *
     * @param to      адрес электронной почты получателя
     * @param subject тема письма
     * @param text    текст письма
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Ошибка при отправке письма: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendVerificationEmail(String toEmail, String verificationLink) {
        String subject = "Подтверждение нового Email";
        String message = "Для подтверждения нового email перейдите по ссылке: " + verificationLink;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
    public void sendPasswordResetEmail(String email, String resetLink) {
        String subject = "Восстановление пароля";
        String message = "Для сброса пароля перейдите по следующей ссылке: " + resetLink;
        sendEmail(email, subject, message);
    }

}