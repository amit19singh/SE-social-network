package org.sn.socialnetwork.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.FriendRequestRepository;
import org.sn.socialnetwork.model.FriendRequest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FriendRequestServiceTest {

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @InjectMocks
    private FriendRequestService friendRequestService;

    public FriendRequestServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendFriendRequest_Success() {
        // Prepare mock data
        User requester = new User();
        User recipient = new User();
        recipient.setId(UUID.randomUUID());
        when(friendRequestRepository.findByRequesterAndRecipient(recipient, requester)).thenReturn(Optional.empty());

        // Mock the save method in repository
        when(friendRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the service method
        friendRequestService.sendFriendRequest(requester, recipient);

        // Verify interactions and assertions
        verify(friendRequestRepository, times(1)).findByRequesterAndRecipient(recipient, requester);
        verify(friendRequestRepository, times(1)).save(any());
    }

//    @Test
//    void sendFriendRequest_AlreadyFriends() {
//        // Prepare mock data
//        User requester = new User();
//        User recipient = new User();
//        recipient.setId(UUID.randomUUID());
//        when(friendRequestRepository.findByRequesterAndRecipient(recipient, requester))
//                .thenReturn(Optional.of(new FriendRequest())); // Simulating existing friendship
//
//        // Call the service method and verify exception
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            friendRequestService.sendFriendRequest(requester, recipient);
//        });
//
//        // Verify interactions
//        verify(friendRequestRepository, times(1)).findByRequesterAndRecipient(recipient, requester);
//        verify(friendRequestRepository, never()).save(any());
//    }

}
