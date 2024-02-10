package org.sn.socialnetwork.service;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.ExceptionHandler.UserNotVerifiedException;
import org.sn.socialnetwork.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.sn.socialnetwork.repository.UserRepository;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with username or email: " + username)
                );

        if (!user.isVerified())
            throw new UserNotVerifiedException("User not verified. Please verify your account before logging in.");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, true, true, true,
                new ArrayList<>()
        );
    }

    public boolean verifyTwoFactorAuthentication(String username, String otp) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().isTwoFactorEnabled()) {
            CodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());
            return verifier.isValidCode(user.get().getTwoFactorSecret(), otp);
        }
        return true; // If 2FA is not enabled, proceed with normal authentication
    }

}


