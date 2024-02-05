package org.sn.socialnetwork.service;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VerificationEmailService {
    final private JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String token) {
        String subject = "Please verify your email";
        String verificationLink = "http://localhost:8080/verify?token=" + token;
        String content = "Click the link to verify your account: " + verificationLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
