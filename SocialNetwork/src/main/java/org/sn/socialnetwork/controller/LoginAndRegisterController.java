package org.sn.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.VerificationToken;
import org.sn.socialnetwork.service.RegisterUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginAndRegisterController {
    final private RegisterUserService registerUserService;
    final private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestParam String usernameOrEmail, @RequestParam String password) {

            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(usernameOrEmail, password);
            Authentication auth = authenticationManager.authenticate(authReq);

            SecurityContextHolder.getContext().setAuthentication(auth);

            // Redirect to a default page after successful login
            String token = "test_token"; // This is just a placeholder

            return ResponseEntity.ok().body(Map.of("message", "Login successful",
                                                    "token", token)); //, "redirectUrl", "/home"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        User registeredUser = registerUserService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        String result = registerUserService.validateVerificationToken(token,
                VerificationToken.TokenType.REGISTRATION_VERIFICATION);

        if ("valid".equals(result)) {
            return ResponseEntity.ok("Account verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }


}



