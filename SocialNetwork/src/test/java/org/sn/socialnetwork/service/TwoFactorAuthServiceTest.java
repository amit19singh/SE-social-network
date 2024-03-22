package org.sn.socialnetwork.service;

import dev.samstevens.totp.code.DefaultCodeVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.service.TwoFactorAuthService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;


import static org.junit.jupiter.api.Assertions.assertTrue;

public class TwoFactorAuthServiceTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TwoFactorAuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testSetupTwoFactorAuth_Success() {
        User user = new User();
        user.setUsername("testuser");

        Mockito.when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));

        String qrUrl = authService.setupTwoFactorAuth("testuser");

        assertNotNull(qrUrl);
        assertTrue(qrUrl.startsWith("data:image/png;base64,"));
    }

//    @Test
//    public void testVerifyOtp_Success() {
//        MockitoAnnotations.initMocks(this);
//
//        // Create a mock user with 2FA setup
//        User user = new User();
//        user.setUsername("testuser");
//        user.setTwoFactorSecret("2FA_SECRET_KEY"); // Set up the 2FA secret key
//        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user));
//
//        // Verify OTP should succeed for the user with 2FA setup
//        boolean result = authService.verifyOtp("testuser", "123456"); // Assuming OTP is valid
//        assertTrue(result);
//    }



    @Test
    public void testVerifyOtp_UserNotFound() {
        when(userRepository.findByUsername(eq("nonexistentuser"))).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authService.verifyOtp("nonexistentuser", "123456"));
    }


    @Test
    public void testVerifyOtp_2FASetupNotComplete() {
        User user = new User();
        user.setUsername("testuser");

        Mockito.when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> authService.verifyOtp("testuser", "123456"));
    }
}
