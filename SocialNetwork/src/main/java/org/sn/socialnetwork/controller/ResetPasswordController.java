package org.sn.socialnetwork.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.dto.LoginRequest;
import org.sn.socialnetwork.dto.PasswordResetRequestDTO;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.service.ResetPasswordService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
//@RequestMapping("/api/forgot-password")
public class ResetPasswordController {
    final private ResetPasswordService resetPasswordService;
    final private UserRepository userRepository;

    @PostMapping("/password-reset-security-check")
    public ResponseEntity<?> checkSecurityAnswers(@RequestParam("email") String email,
                                                  @RequestParam("answer1") String answer1,
                                                  @RequestParam("answer2") String answer2) {
        boolean answersValid = resetPasswordService.verifySecurityAnswers(email, answer1, answer2);
        if (!answersValid) {
            return ResponseEntity.badRequest().body("Security answers do not match.");
        }

        resetPasswordService.createPasswordResetTokenForUser(email);
        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        String result = resetPasswordService.validatePasswordResetToken(passwordResetRequestDTO.getToken());
        if (!result.equals("valid")) {
            return ResponseEntity.badRequest().body("Invalid or expired password reset token");
        }
        User user = resetPasswordService.getUserByPasswordResetToken(passwordResetRequestDTO.getToken());
        resetPasswordService.changeUserPassword(user, passwordResetRequestDTO.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }


    @GetMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestParam("username") String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            System.out.println("Username doesn't exist.");
            return ResponseEntity.badRequest().body("Username doesn't exist.");
        }
        User user = userOptional.get();
        return ResponseEntity.ok(Map.of(
                "usernameExists", true,
                "email", user.getEmail(),
                "SecurityQuestion1", user.getSecurityQuestion1(),
                "SecurityQuestion2", user.getSecurityQuestion2()));
    }


}


