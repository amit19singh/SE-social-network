package org.sn.socialnetwork.service;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.sn.socialnetwork.repository.UserRepository;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElse(userRepository.findByEmail(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found with username or email: " + username)
                        )
                );

        if (!user.isVerified())
            throw new UsernameNotFoundException("User not verified. Please verify your account before logging in.");

        System.out.println("Loaded user by username/email: " + username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, true, true, true,
                new ArrayList<>()
        );
    }
}


