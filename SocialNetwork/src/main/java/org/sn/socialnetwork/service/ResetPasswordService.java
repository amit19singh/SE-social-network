package org.sn.socialnetwork.service;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.VerificationToken;
import org.sn.socialnetwork.model.VerificationToken.TokenType;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResetPasswordService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user found with email: " + email));

        String token = UUID.randomUUID().toString();

        // Check and delete or update existing token
        Optional<VerificationToken> existingToken = tokenRepository.findByUserAndType(user, TokenType.REGISTRATION_VERIFICATION);
        System.out.println(existingToken);
        if (existingToken.isPresent()) {
            VerificationToken myToken = existingToken.get();
            myToken.setToken(token); // Update the token value
            myToken.setExpiryDate(LocalDateTime.now().plusMinutes(1440)); // Reset the expiry time
            tokenRepository.save(myToken);
        } else {
            VerificationToken newToken = new VerificationToken(user, token, TokenType.PASSWORD_RESET);
            tokenRepository.save(newToken);
        }

        emailService.sendPasswordResetEmail(user, token);
    }


    public String validatePasswordResetToken(String token) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);

        if (verificationToken.isEmpty() ||
                verificationToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return "invalid";
        }
        return "valid";
    }

    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Add a method to get User by token if needed
    public User getUserByPasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
                .map(VerificationToken::getUser)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }
}
