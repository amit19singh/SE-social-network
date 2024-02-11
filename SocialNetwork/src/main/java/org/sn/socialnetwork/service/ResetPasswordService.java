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
    private final FieldEncryptor fieldEncryptor;

    public boolean verifySecurityAnswers(String email, String answer1, String answer2) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Encrypt the provided answers to match against the stored encrypted values
//        String encryptedAnswer1 = fieldEncryptor.convertToDatabaseColumn(answer1);
//        String encryptedAnswer2 = fieldEncryptor.convertToDatabaseColumn(answer2);

        return user.getSecurityAnswer1().equals(answer1) &&
                user.getSecurityAnswer2().equals(answer2);
    }

//    public String generateTemporaryVerificationToken(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        String temporaryToken = UUID.randomUUID().toString();
//        VerificationToken verificationToken = new VerificationToken(user, temporaryToken, TokenType.TEMPORARY_VERIFICATION);
//        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Set a suitable expiry time
//        tokenRepository.save(verificationToken);
//
//        return temporaryToken;
//    }

    public String generateTemporaryVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String temporaryToken = UUID.randomUUID().toString();

        // Check for an existing TEMPORARY_VERIFICATION token for the user
        Optional<VerificationToken> existingToken = tokenRepository.findById(user.getId());
        if (existingToken.isPresent()) {
            // If an existing token is found, update it with the new token value and expiry time
            VerificationToken tokenToUpdate = existingToken.get();
            tokenToUpdate.setToken(temporaryToken);
            tokenToUpdate.setExpiryDate(LocalDateTime.now().plusHours(1)); // Update expiry time for the new token
            tokenRepository.save(tokenToUpdate);
        } else {
            // If no existing token is found, create a new one
            VerificationToken newToken = new VerificationToken(user, temporaryToken, TokenType.TEMPORARY_VERIFICATION);
            newToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Set expiry time for the new token
            tokenRepository.save(newToken);
        }

        return temporaryToken;
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
}
