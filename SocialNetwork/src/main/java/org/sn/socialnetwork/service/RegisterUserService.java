package org.sn.socialnetwork.service;


import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.VerificationToken;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.repository.VerificationTokenRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserService {

    final private UserRepository userRepository ;
    final private PasswordEncoder passwordEncoder;
    final private VerificationTokenRepository tokenRepository;
    final private VerificationEmailService verificationEmailService;

    public User registerUser(User user){

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Email already in use. Please use a different Email");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Username already taken");
        }

        if (user.getEmail().chars().filter(ch -> ch == '@').count() != 1) {
            throw new DataIntegrityViolationException("Invalid Email");
        }

        String username = new ArrayList<>(List.of(user.getEmail().split("@"))).get(0);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User registeredUser = userRepository.save(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(registeredUser, token);
        tokenRepository.save(verificationToken);

        // Send verification email
        verificationEmailService.sendVerificationEmail(registeredUser, token);

        return registeredUser;

    }

    public String validateVerificationToken(String token) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);

        if (verificationToken.isEmpty() ||
                verificationToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return "invalid";
        }

        User user = verificationToken.get().getUser();
        user.setVerified(true);
        userRepository.save(user);

        return "valid";
    }

}
