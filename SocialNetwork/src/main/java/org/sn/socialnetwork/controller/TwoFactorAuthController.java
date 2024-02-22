package org.sn.socialnetwork.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.dto.Disable2FADTO;
import org.sn.socialnetwork.dto.Setup2FADTO;
import org.sn.socialnetwork.dto.Verify2FADTO;
import org.sn.socialnetwork.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.sn.socialnetwork.service.TwoFactorAuthService;
import org.sn.socialnetwork.repository.UserRepository;

import java.util.Optional;

@RestController
@AllArgsConstructor
//@RequestMapping("/api/two-factor")
public class TwoFactorAuthController {

    final private TwoFactorAuthService twoFactorAuthService;
    final private UserRepository userRepository;


    @PostMapping("/setup2fa")
    public ResponseEntity<?> setupTwoFactorAuth(@RequestBody Setup2FADTO request) {
        String qrUrl = twoFactorAuthService.setupTwoFactorAuth(request.getUsername());
        System.out.println("In setup2fa controller");
        return ResponseEntity.ok(qrUrl);
    }

    @PostMapping("/verify2fa")
    public ResponseEntity<?> verifyTwoFactorAuth(@RequestBody Verify2FADTO request) {
        boolean isVerified = twoFactorAuthService.verifyOtp(request.getUsername(), request.getOtp());
        System.out.println("In verify2fa controller");
        if (isVerified) {
            Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setTwoFactorEnabled(true);
                userRepository.save(user);
            }
            return ResponseEntity.ok("2FA setup successful");
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid OTP");
        }
    }

    @PostMapping("/disable2fa")
    public ResponseEntity<?> disableTwoFactorAuth(@RequestBody Disable2FADTO request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setTwoFactorEnabled(false);
            user.setTwoFactorSecret(null); // Clear the 2FA secret
            userRepository.save(user);
            return ResponseEntity.ok("2FA disabled successfully");
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("User not found");
        }
    }

}


