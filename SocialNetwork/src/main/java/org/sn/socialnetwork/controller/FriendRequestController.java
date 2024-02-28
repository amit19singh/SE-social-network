package org.sn.socialnetwork.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.dto.FriendRequestDto;
import org.sn.socialnetwork.model.FriendRequest;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.sn.socialnetwork.service.FriendRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

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

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable UUID requestId) {
        System.out.println("requestId: " + requestId);
        try {
            User requestIdUser = userRepository.findById(requestId).orElseThrow(()
                    -> new UserNotFoundException("User not found"));
            FriendRequest friendRequest = friendRequestService.acceptFriendRequest(getUserFromAuth.getCurrentUser(), requestIdUser);
            System.out.println("friendRequest: " + friendRequest);
            return ResponseEntity.ok().body(friendRequest);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable UUID requestId) {
        try {
            User requestIdUser = userRepository.findById(requestId).orElseThrow(()
                    -> new UserNotFoundException("User not found"));;
//            FriendRequest friendRequest = friendRequestService.rejectFriendRequest(getUserFromAuth.getCurrentUser(), requestIdUser);
            friendRequestService.rejectFriendRequest(getUserFromAuth.getCurrentUser(), requestIdUser);
            return ResponseEntity.ok().body("ok");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }
}


