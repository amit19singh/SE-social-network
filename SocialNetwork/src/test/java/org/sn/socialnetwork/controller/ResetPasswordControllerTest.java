package org.sn.socialnetwork.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Map;
import org.mockito.MockitoAnnotations;
import org.sn.socialnetwork.dto.PasswordResetRequestDTO;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.service.ResetPasswordService;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ResetPasswordControllerTest {

    @Mock
    private ResetPasswordService resetPasswordService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResetPasswordController resetPasswordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCheckSecurityAnswers() {
        String email = "def@gmail.com";
        String answer1 = "answer1";
        String answer2 = "answer2";

        doReturn(true).when(resetPasswordService).verifySecurityAnswers(email, answer1, answer2);

        ResponseEntity<?> responseEntity = resetPasswordController.checkSecurityAnswers(email, answer1, answer2);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("Password reset link has been sent to your email.", responseEntity.getBody());
    }

    @Test
    void testResetPassword() {
        String token = "def123";
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO();
        requestDTO.setToken(token);
        requestDTO.setNewPassword("newPassword");

        User user = new User();
        user.setEmail("def@gmail.com");

        doReturn("valid").when(resetPasswordService).validatePasswordResetToken(token);
        doReturn(user).when(resetPasswordService).getUserByPasswordResetToken(token);

        ResponseEntity<?> responseEntity = resetPasswordController.resetPassword(requestDTO);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("Password reset successfully", responseEntity.getBody());
    }

    @Test
    void testCheckUser() {
        String username = "testUser";

        User user = new User();
        user.setEmail("def@gmail.com");
        user.setSecurityQuestion1("Question1");
        user.setSecurityQuestion2("Question2");

        doReturn(java.util.Optional.of(user)).when(userRepository).findByUsername(username);

        ResponseEntity<?> responseEntity = resetPasswordController.checkUser(username);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(true, ((Map<?, ?>) responseEntity.getBody()).get("usernameExists"));
        assertEquals("def@gmail.com", ((Map<?, ?>) responseEntity.getBody()).get("email"));
        assertEquals("Question1", ((Map<?, ?>) responseEntity.getBody()).get("SecurityQuestion1"));
        assertEquals("Question2", ((Map<?, ?>) responseEntity.getBody()).get("SecurityQuestion2"));
    }
}
