package org.sn.socialnetwork.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.ExceptionHandler.UserNotVerifiedException;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.security_and_config.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserFoundAndVerified() {
        String username = "testuser";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setVerified(true);

        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof UserPrincipal);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        String username = "nonexistentuser";

        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));
    }

    @Test
    public void testLoadUserByUsername_UserNotVerified() {
        String username = "unverifieduser";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setVerified(false);

        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(user));

        assertThrows(UserNotVerifiedException.class, () -> customUserDetailsService.loadUserByUsername(username));
    }
}
