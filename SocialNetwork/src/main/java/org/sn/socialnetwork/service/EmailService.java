package org.sn.socialnetwork.service;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

//@Service
//@AllArgsConstructor
@Component
public class EmailService {

    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${app.backend.url}")
    private String backendUrl;

    final private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendEmail(User user, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@example.com");
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendRegisterVerificationEmail(User user, String token) {
        String subject = "Please verify your email";
        String verificationLink = backendUrl + "/verify?token=" + token;
        String content = "Click the link to verify your account: " + verificationLink;
        sendEmail(user, subject, content);
    }

    public void sendPasswordResetEmail(User user, String token) {
        String subject = "Reset your password";
        String passwordResetLink = frontendUrl + "/ResetPasswordPage?token=" + token;
        String content = "You have requested to reset your password. Click the link to proceed: " + passwordResetLink +
                "\nIf you did not request a password reset, please ignore this email or secure your account.";
        sendEmail(user, subject, content);
    }
}


