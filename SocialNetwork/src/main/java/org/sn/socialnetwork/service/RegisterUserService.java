package org.sn.socialnetwork.service;


import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.VerificationToken;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.repository.VerificationTokenRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    public String enableTwoFactorAuthentication(User user) throws QrGenerationException {
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String secret = secretGenerator.generate();
        user.setTwoFactorSecret(secret);
        user.setTwoFactorEnabled(true);
        userRepository.save(user);

        // Generate QR code URL
        QrData qrData = new QrData.Builder()
                .label(user.getEmail()) // 'label' is generally the user's email or username
                .secret(secret)
                .issuer("SocialNetwork") // Replace 'YourAppName' with the name of your application
                .digits(6) // Number of digits in the OTP, 6 is the default
                .period(60) // Validity period of the OTP in seconds, 30 is the default
                .build();

        QrGenerator qrGenerator = new ZxingPngQrGenerator();
        byte[] qrCodeImage = qrGenerator.generate(qrData);

        return Base64.getEncoder().encodeToString(qrCodeImage); // Return this URL to the frontend to display as a QR code
    }

}
