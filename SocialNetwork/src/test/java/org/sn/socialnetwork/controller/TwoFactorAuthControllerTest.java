package org.sn.socialnetwork.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sn.socialnetwork.dto.Setup2FADTO;
import org.sn.socialnetwork.dto.Verify2FADTO;
import org.sn.socialnetwork.dto.Disable2FADTO;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.service.TwoFactorAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TwoFactorAuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TwoFactorAuthService authService;

    @InjectMocks
    private TwoFactorAuthController controller;

    @Test
    public void testSetupTwoFactorAuth_Success() {
        String username = "testUser";
        String qrUrl = "data:image/png;base64,xyz123";
        Setup2FADTO request = new Setup2FADTO();
        request.setUsername(username);

        when(authService.setupTwoFactorAuth(username)).thenReturn(qrUrl);

        ResponseEntity<?> responseEntity = controller.setupTwoFactorAuth(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(qrUrl, responseEntity.getBody());
    }

    @Test
    public void testVerifyTwoFactorAuth_Success() {
        // Arrange
        String username = "testUser";
        String otp = "123456";

        // Mock UserRepository response
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Mock TwoFactorAuthService response
        when(authService.verifyOtp(username, otp)).thenReturn(true);

        // Act
        ResponseEntity<?> response = controller.verifyTwoFactorAuth(new Verify2FADTO(username, otp));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("2FA setup successful", response.getBody());
    }

    @Test
    public void testDisableTwoFactorAuth_Success() {
        // Arrange
        String username = "testUser";

        // Mock UserRepository response
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Act
        ResponseEntity<?> response = controller.disableTwoFactorAuth(new Disable2FADTO(username));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("2FA disabled successfully", response.getBody());
    }
}
