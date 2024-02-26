package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.dto.FriendRequestDto;
import org.sn.socialnetwork.model.FriendRequest;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.sn.socialnetwork.service.FriendRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/friends")
@AllArgsConstructor
public class FriendRequestController {

    final private FriendRequestService friendRequestService;
    final private UserRepository userRepository;
    final private SecurityUtils getUserFromAuth;

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody String recipient) {
        User recipientUser = userRepository.findByUsername(recipient).orElseThrow(()
                                                            -> new UserNotFoundException("User not found"));;
        User requester = getUserFromAuth.getCurrentUser();
        FriendRequest friendRequest = friendRequestService.sendFriendRequest(requester, recipientUser);
        return ResponseEntity.ok().body(friendRequest);
    }

    // Endpoints for accepting and rejecting friend requests
}


