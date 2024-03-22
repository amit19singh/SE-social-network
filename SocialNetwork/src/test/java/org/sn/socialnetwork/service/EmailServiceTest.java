package org.sn.socialnetwork.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;

import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailService emailService;

    public EmailServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendRegisterVerificationEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        String token = "verificationToken";

        emailService.sendRegisterVerificationEmail(user, token);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendPasswordResetEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        String token = "resetToken";

        emailService.sendPasswordResetEmail(user, token);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
