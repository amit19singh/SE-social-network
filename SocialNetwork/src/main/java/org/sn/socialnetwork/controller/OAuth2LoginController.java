package org.sn.socialnetwork.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.sn.socialnetwork.repository.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class OAuth2LoginController {

    final private UserRepository userRepository;
    final private Random random = new SecureRandom();

    @GetMapping("/custom-login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
            String email = (String) attributes.get("email");
            // Check if user exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isEmpty()) {
                // Create and save the new user
                User newUser = new User();
                newUser.setFirstname((String) attributes.get("given_name"));
                newUser.setLastname((String) attributes.get("family_name"));
                newUser.setEmail(email);
                newUser.setUsername(new ArrayList<>(List.of(email.split("@"))).get(0));
                newUser.setPassword(crypt());
                newUser.setBirthday(LocalDate.now().minusYears(25));
                newUser.setVerified(true);
                newUser.setGender("Unspecified");
                newUser.setCreatedAt(LocalDateTime.now());
                newUser.setSecurityQuestion1(crypt());
                newUser.setSecurityAnswer1(crypt());
                newUser.setSecurityQuestion2(crypt());
                newUser.setSecurityAnswer2(crypt());
                userRepository.save(newUser);
//              Redirect here to complete the profile
            }
        }
        return "home";
    }

    public String crypt() {
        return random.ints(48, 33, 127)
                .mapToObj(i -> String.valueOf((char)i))
                .collect(Collectors.joining());
    }

}
