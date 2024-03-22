package org.sn.socialnetwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sn.socialnetwork.ExceptionHandler.EmailAlreadyInUseException;
import org.sn.socialnetwork.ExceptionHandler.UsernameAlreadyInUseException;
import org.sn.socialnetwork.dto.LoginRequest;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.security_and_config.JwtAuthenticationResponse;
import org.sn.socialnetwork.security_and_config.JwtTokenProvider;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.sn.socialnetwork.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAuthenticateUser() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwtToken");

        ResponseEntity<?> response = userController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(JwtAuthenticationResponse.class, Objects.requireNonNull(response.getBody()).getClass());
    }

    @Test
    public void testRegisterUser_Success() throws EmailAlreadyInUseException, UsernameAlreadyInUseException {
        User user = new User();
        when(userService.registerUser(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userController.registerUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(User.class, Objects.requireNonNull(response.getBody()).getClass());
    }

    @Test
    public void testRegisterUser_EmailAlreadyInUseException() throws EmailAlreadyInUseException, UsernameAlreadyInUseException {
        User user = new User();
        when(userService.registerUser(any(User.class))).thenThrow(new EmailAlreadyInUseException("Email already in use"));

        ResponseEntity<?> response = userController.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already in use", response.getBody());
    }

    @Test
    public void testRegisterUser_UsernameAlreadyInUseException() throws EmailAlreadyInUseException, UsernameAlreadyInUseException {
        User user = new User();
        when(userService.registerUser(any(User.class))).thenThrow(new UsernameAlreadyInUseException("Username already in use"));

        ResponseEntity<?> response = userController.registerUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already in use", response.getBody());
    }

    @Test
    public void testVerifyAccount() {
        RedirectView redirectView = userController.verifyAccount("token");

        assertEquals(RedirectView.class, redirectView.getClass());
    }

}
