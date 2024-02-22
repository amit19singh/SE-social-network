package org.sn.socialnetwork.security_and_config;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component
public class SecurityUtils {
    private final UserRepository userRepository;

    public static UserPrincipal getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        throw new IllegalStateException("No user authenticated");
    }

    public User getCurrentUser() {
        UserPrincipal userPrincipal = getCurrentUserDetails();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + userPrincipal.getId()));
    }

    public UUID getCurrentUserId() {
        UserPrincipal userPrincipal = getCurrentUserDetails();
        return userPrincipal.getId();
    }
}