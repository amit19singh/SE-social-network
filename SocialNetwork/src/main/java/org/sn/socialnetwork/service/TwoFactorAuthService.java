package org.sn.socialnetwork.service;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.sn.socialnetwork.model.User;

import java.util.Base64;

@Service
@AllArgsConstructor
public class TwoFactorAuthService {

    final private UserRepository userRepository;

    public String setupTwoFactorAuth(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        String secret = new DefaultSecretGenerator().generate();
        user.setTwoFactorSecret(secret);
        userRepository.save(user);

        QrData qrData = new QrData.Builder()
                .label(user.getEmail())
                .secret(secret)
                .issuer("SocialNetwork")
                .digits(6)
                .period(1800) // QR will be valid for 30 minutes
                .build();

        QrGenerator qrGenerator = new ZxingPngQrGenerator();
        try {
            byte[] qrCodeImage = qrGenerator.generate(qrData);
            String qrCodeUrl = Base64.getEncoder().encodeToString(qrCodeImage);
            return "data:image/png;base64," + qrCodeUrl;
        } catch (QrGenerationException e) {
            throw new IllegalStateException("QR Code generation failed", e);
        }
    }

    public boolean verifyOtp(String username, String otp) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getTwoFactorSecret() == null) {
            throw new IllegalStateException("2FA is not set up for this user");
        }

        DefaultCodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());
        return verifier.isValidCode(user.getTwoFactorSecret(), otp);
    }
}
