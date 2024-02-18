package org.sn.socialnetwork.service;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.VerificationToken;
import org.sn.socialnetwork.model.VerificationToken.TokenType;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.repository.VerificationTokenRepository;
import org.sn.socialnetwork.security_and_config.FieldEncryptor;
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

    public boolean verifySecurityAnswers(String email, String answer1, String answer2) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getSecurityAnswer1().equals(answer1) &&
                user.getSecurityAnswer2().equals(answer2);
    }


    public String generateTemporaryVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String temporaryToken = UUID.randomUUID().toString();

        // Check for an existing TEMPORARY_VERIFICATION token for the user
        Optional<VerificationToken> existingToken = tokenRepository.findByUserId(user.getId());
        if (existingToken.isPresent()) {
            // If an existing token is found, update it with the new token value and expiry time
            VerificationToken tokenToUpdate = existingToken.get();
            tokenToUpdate.setToken(temporaryToken);
            tokenToUpdate.setType(TokenType.TEMPORARY_VERIFICATION);
            tokenToUpdate.setExpiryDate(LocalDateTime.now().plusMinutes(1)); // Update expiry time for the new token
            tokenRepository.save(tokenToUpdate);
        } else {
            // If no existing token is found, create a new one
            VerificationToken newToken = new VerificationToken(user, temporaryToken, TokenType.TEMPORARY_VERIFICATION);
            newToken.setExpiryDate(LocalDateTime.now().plusMinutes(1)); // Set expiry time for the new token
            tokenRepository.save(newToken);
        }

        return temporaryToken;
    }

    public boolean verifyUserEligibilityForPasswordReset(String email, String verificationToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<VerificationToken> tokenOpt = tokenRepository.
                findByTokenAndUserAndType(verificationToken, user, TokenType.TEMPORARY_VERIFICATION);

        if (tokenOpt.isPresent() && tokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            tokenRepository.delete(tokenOpt.get());
            return true;
        }
        return false;
    }

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
            myToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // Reset the expiry time
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

    // Add a method to get User by token if needed
    public User getUserByPasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
                .map(VerificationToken::getUser)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
