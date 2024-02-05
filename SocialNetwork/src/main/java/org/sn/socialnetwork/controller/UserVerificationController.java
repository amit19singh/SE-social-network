package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.service.RegisterUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserVerificationController {
    final RegisterUserService registerUserService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        String result = registerUserService.validateVerificationToken(token);

        if ("valid".equals(result)) {
            return ResponseEntity.ok("Account verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }
}