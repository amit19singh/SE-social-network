package org.sn.socialnetwork.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.repository.VerificationTokenRepository;
import org.sn.socialnetwork.security_and_config.FieldEncryptor;
import org.sn.socialnetwork.service.ResetPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.sn.socialnetwork.security_and_config.EmailService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResetPasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testVerifySecurityAnswers() {
        String email = "test@example.com";
        String answer1 = "answer1";
        String answer2 = "answer2";

        User user = new User();
        user.setEmail(email);
        user.setSecurityAnswer1(answer1);
        user.setSecurityAnswer2(answer2);

        doReturn(java.util.Optional.of(user)).when(userRepository).findByEmail(email);

        assertTrue(resetPasswordService.verifySecurityAnswers(email, answer1, answer2));
    }

    @Test
    void testGenerateTemporaryVerificationToken() {
        String email = "test@example.com";

        User user = new User();
        user.setId(UUID.randomUUID());

        doReturn(java.util.Optional.of(user)).when(userRepository).findByEmail(email);

        String token = resetPasswordService.generateTemporaryVerificationToken(email);

        assertNotNull(token);
    }

    // Add more test cases for other methods as needed
}
