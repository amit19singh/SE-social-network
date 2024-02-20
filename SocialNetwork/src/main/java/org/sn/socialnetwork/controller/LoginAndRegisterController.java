package org.sn.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.EmailAlreadyInUseException;
import org.sn.socialnetwork.ExceptionHandler.UsernameAlreadyInUseException;
import org.sn.socialnetwork.dto.LoginRequest;
import org.sn.socialnetwork.model.JwtAuthenticationResponse;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.VerificationToken;
import org.sn.socialnetwork.security_and_config.JwtTokenProvider;
import org.sn.socialnetwork.service.RegisterUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginAndRegisterController {
    final private RegisterUserService registerUserService;
    final private AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${app.backend.url}")
    private String backendUrl;

//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> authenticateUser(@RequestParam String usernameOrEmail, @RequestParam String password) {
//
//            UsernamePasswordAuthenticationToken authReq
//                    = new UsernamePasswordAuthenticationToken(usernameOrEmail, password);
//            Authentication auth = authenticationManager.authenticate(authReq);
//
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            // Redirect to a default page after successful login
//            String token = "test_token"; // This is just a placeholder
//
//            return ResponseEntity.ok().body(Map.of("message", "Login successful",
//                                                    "token", token)); //, "redirectUrl", "/home"));
//    }
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody LoginRequest loginRequest) {
//        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
//        Authentication auth = authenticationManager.authenticate(authReq);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        String token = jwtTokenProvider.generateToken(auth); // Use your JwtTokenProvider to generate a token
//        return ResponseEntity.ok().body(Map.of("message", "Login successful", "token", token));
//    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        try {
            User registeredUser = registerUserService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (EmailAlreadyInUseException | UsernameAlreadyInUseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public RedirectView verifyAccount(@RequestParam("token") String token) {
        String result = registerUserService.validateVerificationToken(token,
                VerificationToken.TokenType.REGISTRATION_VERIFICATION);

        String frontENDUrl;
        if ("valid".equals(result)) {
            frontENDUrl = frontendUrl + "/home";
        } else {
            frontENDUrl = frontendUrl + "/verification-failure";
        }
        return new RedirectView(frontENDUrl);
    }

//  The following is only for API Testing

//    @GetMapping("/verify")
//    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
//        String result = registerUserService.validateVerificationToken(token,
//                VerificationToken.TokenType.REGISTRATION_VERIFICATION);
//
//        if ("valid".equals(result)) {
//            return ResponseEntity.ok("Account verified successfully!");
//        } else {
//            return ResponseEntity.badRequest().body("Invalid or expired token.");
//        }
//    }

}


