package org.sn.socialnetwork.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sn.socialnetwork.service.TwoFactorAuthService;

@RestController
@AllArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;

    @PostMapping("/setup2fa")
    public ResponseEntity<?> setupTwoFactorAuth(@RequestParam("username") String username) {
        String qrUrl = twoFactorAuthService.setupTwoFactorAuth(username);
        return ResponseEntity.ok(qrUrl); // Return QR URL to the client
    }

    @PostMapping("/verify2fa")
    public ResponseEntity<?> verifyTwoFactorAuth(@RequestParam("username") String username, @RequestParam("otp") String otp) {
        boolean isVerified = twoFactorAuthService.verifyOtp(username, otp);
        if (isVerified) {
            return ResponseEntity.ok("2FA setup successful");
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid OTP");
        }
    }
}
