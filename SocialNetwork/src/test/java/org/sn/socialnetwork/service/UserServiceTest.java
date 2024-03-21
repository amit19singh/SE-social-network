//this is wrong
package org.sn.socialnetwork.service;

//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.sn.socialnetwork.ExceptionHandler.EmailAlreadyInUseException;
//import org.sn.socialnetwork.model.User;
//import org.sn.socialnetwork.repository.UserRepository;
//import org.sn.socialnetwork.repository.VerificationTokenRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private VerificationTokenRepository tokenRepository;
//
//    @Mock
//    private EmailService emailService;
//
//    @InjectMocks
//    private UserService userService;
//
//    @Test
//    public void testRegisterUser_Success() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password123");
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
//
//        User registeredUser = userService.registerUser(user);
//
//        assertNotNull(registeredUser);
//        assertEquals("test@example.com", registeredUser.getEmail());
//        assertEquals("encodedPassword", registeredUser.getPassword());
//        verify(userRepository, times(1)).save(user);
//        verify(tokenRepository, times(1)).save(any()); // Verify that tokenRepository.save() is called
//        verify(emailService, times(1)).sendVerificationEmail(user, anyString()); // Verify that emailService.sendVerificationEmail() is called
//    }
//
//    @Test
//    public void testRegisterUser_EmailAlreadyInUse() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password123");
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//
//        assertThrows(EmailAlreadyInUseException.class, () -> userService.registerUser(user));
//        verify(userRepository, never()).save(user);
//        verify(tokenRepository, never()).save(any());
//        verify(emailService, never()).sendVerificationEmail(any(), anyString());
//    }
//
//    @Test
//    public void testRegisterUser_UsernameAlreadyInUse() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password123");
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.empty());
//        when(userRepository.CheckUsernameExists("test")).thenReturn(true);
//
//        assertThrows(UsernameAlreadyInUseException.class, () -> userService.registerUser(user));
//        verify(userRepository, never()).save(user);
//        verify(passwordEncoder, never()).encode(anyString());
//    }
}
