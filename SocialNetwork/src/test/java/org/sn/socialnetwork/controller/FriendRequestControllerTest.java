package org.sn.socialnetwork.controller;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.sn.socialnetwork.service.FriendRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.sn.socialnetwork.model.FriendRequest;


import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FriendRequestControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRequestService friendRequestService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private FriendRequestController friendRequestController;

    public FriendRequestControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendFriendRequest_Success() {
        // Prepare mock data
        User recipientUser = new User();
        recipientUser.setUsername("tara");
        User requester = new User();
        when(userRepository.findByUsername("tara")).thenReturn(Optional.of(recipientUser));
        when(securityUtils.getCurrentUser()).thenReturn(requester);

        // Mock the service method call
        when(friendRequestService.sendFriendRequest(requester, recipientUser)).thenReturn(new FriendRequest());

        // Call the controller method
        ResponseEntity<?> response = friendRequestController.sendFriendRequest("tara");

        // Verify the interactions and assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("tara");
        verify(friendRequestService, times(1)).sendFriendRequest(requester, recipientUser);
    }

}
