package org.sn.socialnetwork.service;

//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.sn.socialnetwork.model.User;
//import org.sn.socialnetwork.repository.UserRepository;
//import org.sn.socialnetwork.service.CustomOAuth2UserService;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
public class CustomOAuth2UserServiceTest {
}
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ClientRegistrationRepository clientRegistrationRepository;
//
//    @InjectMocks
//    private CustomOAuth2UserService customOAuth2UserService;
//
//    @Test
//    public void testLoadUser_NewUser() {
//        // Prepare mock data
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("email", "test@example.com");
//        attributes.put("given_name", "John");
//        attributes.put("family_name", "Doe");
//
//        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
//        when(userInfoEndpoint.getUri()).thenReturn("https://example.com/userinfo");
//
//        ClientRegistration.ProviderDetails providerDetails = mock(ClientRegistration.ProviderDetails.class);
//        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
//
//        ClientRegistration clientRegistration = mock(ClientRegistration.class);
//        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
//        when(clientRegistrationRepository.findByRegistrationId("google")).thenReturn(clientRegistration);
//
//        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, mock(OAuth2AccessToken.class));
//        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//
//        // Call the method to be tested
//        OAuth2User result = customOAuth2UserService.loadUser(userRequest);
//
//        // Verify interactions and assertions
//        verify(userRepository, times(1)).findByEmail("test@example.com");
//        verify(userRepository, times(1)).save(any(User.class));
//        assertEquals(oAuth2User, result);
//    }
//
//    @Test
//    public void testLoadUser_ExistingUser() {
//        // Prepare mock data
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("email", "test@example.com");
//        attributes.put("given_name", "John");
//        attributes.put("family_name", "Doe");
//
//        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
//        when(userInfoEndpoint.getUri()).thenReturn("https://example.com/userinfo");
//
//        ClientRegistration.ProviderDetails providerDetails = mock(ClientRegistration.ProviderDetails.class);
//        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
//
//        ClientRegistration clientRegistration = mock(ClientRegistration.class);
//        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
//        when(clientRegistrationRepository.findByRegistrationId("google")).thenReturn(clientRegistration);
//
//        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, mock(OAuth2AccessToken.class));
//        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
//
//        // Call the method to be tested
//        OAuth2User result = customOAuth2UserService.loadUser(userRequest);
//
//        // Verify interactions and assertions
//        verify(userRepository, times(1)).findByEmail("test@example.com");
//        verify(userRepository, never()).save(any(User.class));
//        assertEquals(oAuth2User, result);
//    }
//}
