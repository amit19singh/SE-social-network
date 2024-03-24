//this is wrong
package org.sn.socialnetwork.service;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Optional;

import org.mockito.MockitoAnnotations;
import org.sn.socialnetwork.ExceptionHandler.EmailAlreadyInUseException;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.ExceptionHandler.UsernameAlreadyInUseException;
import org.sn.socialnetwork.dto.UserDTO;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.sn.socialnetwork.repository.VerificationTokenRepository;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private UserService userService;


    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testRegisterUser() {
        // Mock data
        User user = new User();
        user.setEmail("abc@gmail.com");
        user.setPassword("password");

        // Stub UserRepository and PasswordEncoder
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.CheckUsernameExists(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Test registerUser method
        User registeredUser = userService.registerUser(user);

        assertNotNull(registeredUser);
        assertEquals("abc", registeredUser.getUsername());

        String encodedPassword = registeredUser.getPassword();
        System.out.println("Encoded Password: " + encodedPassword);

        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPasswordHash");

    }


    @Test
    public void testRegisterUserWithEmailAlreadyInUse() {
        // Mock data
        User user = new User();
        user.setEmail("abc@gmail.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Test registerUser method with existing email
        assertThrows(EmailAlreadyInUseException.class, () -> userService.registerUser(user));
    }

    @Test
    public void testRegisterUserWithUsernameAlreadyInUse() {
        // Mock data
        User user = new User();
        user.setEmail("abc@gmail.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.CheckUsernameExists(anyString())).thenReturn(true);

        // Test registerUser method with existing username
        assertThrows(UsernameAlreadyInUseException.class, () -> userService.registerUser(user));
    }

}
