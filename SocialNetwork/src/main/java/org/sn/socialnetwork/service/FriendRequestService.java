package org.sn.socialnetwork.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.FriendRequest;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.FriendRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendRequestService {

    private FriendRequestRepository friendRequestRepository;

    public FriendRequest sendFriendRequest(User requester, User recipient) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequester(requester);
        friendRequest.setRecipient(recipient);
        friendRequest.setStatus(FriendRequest.RequestStatus.PENDING);
        friendRequest.setCreatedAt(LocalDateTime.now());
        // Save the friend request
        return friendRequestRepository.save(friendRequest);
    }

    public FriendRequest acceptFriendRequest(Long requestId) {
        Optional<FriendRequest> friendRequestOpt = friendRequestRepository.findById(requestId);
        if (friendRequestOpt.isPresent()) {
            FriendRequest friendRequest = friendRequestOpt.get();
            friendRequest.setStatus(FriendRequest.RequestStatus.ACCEPTED);
            friendRequest.setUpdatedAt(LocalDateTime.now());
            return friendRequestRepository.save(friendRequest);
        } else {
            throw new EntityNotFoundException("Friend request not found");
        }
    }

    public FriendRequest rejectFriendRequest(Long requestId) {
        Optional<FriendRequest> friendRequestOpt = friendRequestRepository.findById(requestId);
        if (friendRequestOpt.isPresent()) {
            FriendRequest friendRequest = friendRequestOpt.get();
            friendRequest.setStatus(FriendRequest.RequestStatus.REJECTED);
            friendRequest.setUpdatedAt(LocalDateTime.now());
            return friendRequestRepository.save(friendRequest);
        } else {
            throw new EntityNotFoundException("Friend request not found");
        }
    }
}
