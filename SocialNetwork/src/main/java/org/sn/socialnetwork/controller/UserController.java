package org.sn.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.EmailAlreadyInUseException;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.ExceptionHandler.UsernameAlreadyInUseException;
import org.sn.socialnetwork.dto.DisplayUserPostDTO;
import org.sn.socialnetwork.dto.LoginRequest;
import org.sn.socialnetwork.dto.UserDTO;
import org.sn.socialnetwork.dto.UserPostDTO;
import org.sn.socialnetwork.repository.UserPostRepository;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.security_and_config.JwtAuthenticationResponse;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.sn.socialnetwork.security_and_config.UserPrincipal;
import org.sn.socialnetwork.model.VerificationToken;
import org.sn.socialnetwork.security_and_config.JwtTokenProvider;
import org.sn.socialnetwork.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/admission-access")
public class UserController {
    final private UserService userService;
    final private AuthenticationManager authenticationManager;
    final private JwtTokenProvider jwtTokenProvider;
    final private SecurityUtils getUserFromAuth;

    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${app.backend.url}")
    private String backendUrl;

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
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (EmailAlreadyInUseException | UsernameAlreadyInUseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public RedirectView verifyAccount(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token,
                VerificationToken.TokenType.REGISTRATION_VERIFICATION);

        String frontENDUrl;
        if ("valid".equals(result)) {
            frontENDUrl = frontendUrl + "/";
        } else {
            frontENDUrl = frontendUrl + "/verification-failure";
        }
        return new RedirectView(frontENDUrl);
    }

    @GetMapping("/userDetail")
    public ResponseEntity<UserDTO> userDetail(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        UserDTO userDTO = userService.getUserDetailsWithPosts(userPrincipal.getId());

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping(value="/updateProfile", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfile(@ModelAttribute UserDTO userDTO) throws IOException {
        User updatedUser = userService.updateUser(getUserFromAuth.getCurrentUser().getId(), userDTO);
        return ResponseEntity.ok(updatedUser);
    }
}


