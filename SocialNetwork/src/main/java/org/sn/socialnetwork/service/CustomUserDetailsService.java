package org.sn.socialnetwork.service;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.ExceptionHandler.UserNotVerifiedException;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.security_and_config.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.sn.socialnetwork.repository.UserRepository;

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

        return new UserPrincipal(user);
    }


}

