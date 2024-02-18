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

//    @GetMapping("/reset-password")
////    public ResponseEntity<?> validateResetPasswordToken(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
//    public ResponseEntity<?> validateResetPasswordToken(@RequestParam("token") String token, HttpServletResponse response) {
//        String result = resetPasswordService.validatePasswordResetToken(token);
//        if (!result.equals("valid")) {
//            // Redirect to a frontend error page with an appropriate message
//            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "http://localhost:3000/password-reset-error?reason=invalidToken").build();
//        }
////        User user = resetPasswordService.getUserByPasswordResetToken(passwordResetRequestDTO.getToken());
////        resetPasswordService.changeUserPassword(user, passwordResetRequestDTO.getNewPassword());
//        // Redirect to the password reset page with the token as a query parameter
//        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "http://localhost:3000/ResetPasswordPage?token=" + token).build();
//    }

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





//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
//                                           @RequestParam("newPassword") String newPassword) {
//        String result = resetPasswordService.validatePasswordResetToken(token);
//        if (!result.equals("valid")) {
//            return ResponseEntity.badRequest().body("Invalid or expired password reset token");
//        }
//
//        User user = resetPasswordService.getUserByPasswordResetToken(token);
//        resetPasswordService.changeUserPassword(user, newPassword);
//        return ResponseEntity.ok("Password reset successfully");
//    }

//    This is to check if user exists. If so, then we proceed with the Security answers
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


