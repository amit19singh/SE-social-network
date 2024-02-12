package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.service.ResetPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ResetPasswordController {
    final private ResetPasswordService resetPasswordService;

    @PostMapping("/password-reset-security-check")
    public ResponseEntity<?> checkSecurityAnswers(@RequestParam("email") String email,
                                                  @RequestParam("answer1") String answer1,
                                                  @RequestParam("answer2") String answer2) {
        boolean answersValid = resetPasswordService.verifySecurityAnswers(email, answer1, answer2);
        if (!answersValid) {
            return ResponseEntity.badRequest().body("Security answers do not match.");
        }

        String temporaryVerificationToken = resetPasswordService.generateTemporaryVerificationToken(email);
        return ResponseEntity.ok(Map.of("message", "Security answers verified, proceed with password reset.",
                "temporaryVerificationToken", temporaryVerificationToken));
    }


    @PostMapping("/password-reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestParam("email") String email,
                                                  @RequestParam("verificationToken") String verificationToken) {
        // Verify the temporary verification token/flag
        boolean isEligible = resetPasswordService.verifyUserEligibilityForPasswordReset(email, verificationToken);
        if (!isEligible) {
            return ResponseEntity.badRequest().body("Security verification failed or expired. Please complete security questions again.");
        }

        // Proceed with creating and sending the password reset token
        resetPasswordService.createPasswordResetTokenForUser(email);
        return ResponseEntity.ok("Password reset link has been sent to your email.");
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


