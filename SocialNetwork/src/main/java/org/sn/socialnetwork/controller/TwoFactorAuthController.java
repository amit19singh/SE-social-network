package org.sn.socialnetwork.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sn.socialnetwork.service.TwoFactorAuthService;
import org.sn.socialnetwork.repository.UserRepository;

@RestController
@AllArgsConstructor
public class TwoFactorAuthController {

    final private TwoFactorAuthService twoFactorAuthService;

    @PostMapping("/setup2fa")
    public ResponseEntity<?> setupTwoFactorAuth(@RequestParam("username") String username) {
        String qrUrl = twoFactorAuthService.setupTwoFactorAuth(username);
        System.out.println("In setup2fa controller");
        return ResponseEntity.ok(qrUrl); // Return QR URL to the client
    }

    @PostMapping("/verify2fa")
    public ResponseEntity<?> verifyTwoFactorAuth(@RequestParam("username") String username, @RequestParam("otp") String otp) {
        boolean isVerified = twoFactorAuthService.verifyOtp(username, otp);
        System.out.println("In verify2fa controller");
        if (isVerified) {
            return ResponseEntity.ok("2FA setup successful");
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid OTP");
        }
    }
}


