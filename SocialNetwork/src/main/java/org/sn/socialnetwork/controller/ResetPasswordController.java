package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.service.ResetPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ResetPasswordController {
    final private ResetPasswordService resetPasswordService;

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestParam("email") String email) {
        resetPasswordService.createPasswordResetTokenForUser(email);
        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    @PostMapping("/validate-password-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam("token") String token) {
        String result = resetPasswordService.validatePasswordResetToken(token);
        if (!result.equals("valid")) {
            return ResponseEntity.badRequest().body("Invalid or expired password reset token");
        }
        return ResponseEntity.ok("Token valid");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestParam("newPassword") String newPassword) {
        String result = resetPasswordService.validatePasswordResetToken(token);
        if (!result.equals("valid")) {
            return ResponseEntity.badRequest().body("Invalid or expired password reset token");
        }

        User user = resetPasswordService.getUserByPasswordResetToken(token);
        resetPasswordService.changeUserPassword(user, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }

}
