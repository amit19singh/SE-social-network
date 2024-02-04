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
public class UserService {

    final private UserRepository userRepository ;
    final private PasswordEncoder passwordEncoder;
    final private VerificationTokenRepository tokenRepository;
    final private EmailService emailService;

    public User registerUser(User user) throws Exception{

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Email already in use. Please use a different Email");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Username already taken");
        }
        try {
            String username = new ArrayList<>(List.of(user.getEmail().split("@"))).get(0);
            user.setUsername(username);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Invalid Email.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);

//        User registeredUser = userRepository.save(user);
//
//        // Generate verification token
//        String token = UUID.randomUUID().toString();
//        VerificationToken verificationToken = new VerificationToken(registeredUser, token);
//        tokenRepository.save(verificationToken);
//
//        // Send verification email
//        emailService.sendVerificationEmail(registeredUser, token);
//
//        return registeredUser;

    }

    public String validateVerificationToken(String token) {
        Optional<VerificationToken> verificationTokenOptional = tokenRepository.findByToken(token);

        if (verificationTokenOptional.isEmpty() ||
                verificationTokenOptional.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return "invalid";
        }

        User user = verificationTokenOptional.get().getUser();
        user.setVerified(true);
        userRepository.save(user);

        return "valid";
    }




//    public User loginUser(User user) {
//
//    }
}
