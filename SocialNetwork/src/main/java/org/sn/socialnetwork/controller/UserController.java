package org.sn.socialnetwork.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.service.RegisterUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    final private RegisterUserService registerUserService;
    final private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestParam String usernameOrEmail, @RequestParam String password) {
        try {
            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(usernameOrEmail, password);
            Authentication auth = authenticationManager.authenticate(authReq);

            SecurityContextHolder.getContext().setAuthentication(auth);

            // Redirect to a default page after successful login
            String token = "test_token"; // This is just a placeholder

            System.out.println("LOGIN SUCCESSFUL");
            return ResponseEntity.ok().body(Map.of("message", "Login successful", "token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                    body(Map.of("error", "Invalid username/email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) throws Exception {
        User registeredUser = registerUserService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

}



