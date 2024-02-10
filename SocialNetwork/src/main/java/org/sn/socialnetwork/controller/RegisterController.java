package org.sn.socialnetwork.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.service.RegisterUserService;
import org.sn.socialnetwork.service.TwoFactorAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RegisterController {
    final private RegisterUserService registerUserService;
    final private AuthenticationManager authenticationManager;


//    @GetMapping("/home")
//    public String home() {
//        return "forward:/home.html";
//    }
//
//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }

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


}



