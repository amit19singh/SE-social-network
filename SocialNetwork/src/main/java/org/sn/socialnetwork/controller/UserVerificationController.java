package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserVerificationController {
    final UserService userService;

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);

        if(result.equals("valid")) {
            return "Account verified successfully!";
        } else {
            return "Invalid token.";
        }
    }
}