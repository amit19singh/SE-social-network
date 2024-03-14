package org.sn.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.EmailAlreadyInUseException;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.ExceptionHandler.UsernameAlreadyInUseException;
import org.sn.socialnetwork.dto.*;
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
import java.util.List;
import java.util.UUID;

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

//    USER-LOGIN
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

//    USER-REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (EmailAlreadyInUseException | UsernameAlreadyInUseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    EMAIL VERIFICATION AFTER REGISTRATION
    @GetMapping("/verify")
    public RedirectView verifyAccount(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token,
                VerificationToken.TokenType.REGISTRATION_VERIFICATION);

        String frontENDUrl;
        if ("valid".equals(result)) {
            frontENDUrl = frontendUrl + "/account_verified";
        } else {
            frontENDUrl = frontendUrl + "/verification-failure";
        }
        return new RedirectView(frontENDUrl);
    }

//    USER DETAILS FOR THE USER HOME PAGE
    @GetMapping("/userDetail")
    public ResponseEntity<UserDTO> userDetail(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        UserPrincipal userPrincipal = (UserPrincipal) userDetails;
        UserDTO userDTO = userService.getUserDetailsWithPostsAndFriends(userPrincipal.getId());

        return ResponseEntity.ok(userDTO);
    }

//    EDIT USER PROFILE
    @PostMapping(value="/updateProfile", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfile(@ModelAttribute UserDTO userDTO) throws IOException {
        User updatedUser = userService.updateUser(getUserFromAuth.getCurrentUser().getId(), userDTO);
        return ResponseEntity.ok(updatedUser);
    }

//    SEARCH FOR PEOPLE/POSTS
    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> searchUsers(@RequestParam String query) {
        SearchResultDTO searchResultDTOS = userService.searchUsersWithCriteriaAPI(query);
        return ResponseEntity.ok(searchResultDTOS);
    }

//    MAKE PROFILE PUBLIC/PRIVATE
    @PostMapping("/updateProfileVisibility")
    public ResponseEntity<?> updateProfileVisibility(@AuthenticationPrincipal UserDetails userDetails, @RequestBody boolean isProfilePublic) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        userService.updateProfileVisibility(userDetails.getUsername(), isProfilePublic);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user/profile/{ownerUserName}")
    public ResponseEntity<UserDTO> getUserProfileForViewer(@PathVariable String ownerUserName) {
        System.out.println("YES in getUserProfileForViewer");
        try {
            UserDTO userDTO = userService.getUserProfileForViewer(ownerUserName);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}

