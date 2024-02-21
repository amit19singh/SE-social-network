package org.sn.socialnetwork.service;

import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    final private UserRepository userRepository;
    final private Random random = new SecureRandom();

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
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
        }

        return oAuth2User;
    }

    public String crypt() {
        return random.ints(48, 33, 127)
                .mapToObj(i -> String.valueOf((char)i))
                .collect(Collectors.joining());
    }
}