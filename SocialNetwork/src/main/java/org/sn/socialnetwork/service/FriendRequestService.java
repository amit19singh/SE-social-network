package org.sn.socialnetwork.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.FriendRequest;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.repository.FriendRequestRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendRequestService {

    private FriendRequestRepository friendRequestRepository;

    public FriendRequest sendFriendRequest(User requester, User recipient) {
        Optional<FriendRequest> exitsingFriendRequest = friendRequestRepository.findByRequesterAndRecipient(recipient, requester);
        if (exitsingFriendRequest.isPresent() && !exitsingFriendRequest.get().getStatus().equals(FriendRequest.RequestStatus.REJECTED))
            throw new DataIntegrityViolationException("Either already Friends or Pending request");
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequester(requester);
        friendRequest.setRecipient(recipient);
        friendRequest.setStatus(FriendRequest.RequestStatus.PENDING);
        friendRequest.setCreatedAt(LocalDateTime.now());
        // Save the friend request
        return friendRequestRepository.save(friendRequest);
    }

//    I'm Recipient as I will be accepting/rejecting requests
    public FriendRequest acceptFriendRequest(User currUser, User requestId) {
        Optional<FriendRequest> friendRequestOpt = friendRequestRepository.findByRequesterAndRecipient(requestId, currUser);
        System.out.println("friendRequestOpt: " + friendRequestOpt);
        if (friendRequestOpt.isPresent() && friendRequestOpt.get().getStatus().equals(FriendRequest.RequestStatus.PENDING)) {
            System.out.println("YES");
            FriendRequest friendRequest = friendRequestOpt.get();
            friendRequest.setStatus(FriendRequest.RequestStatus.ACCEPTED);
            friendRequest.setUpdatedAt(LocalDateTime.now());
            return friendRequestRepository.save(friendRequest);
        } else {
            throw new EntityNotFoundException("Friend request not found");
        }
    }

    public void rejectFriendRequest(User currUser, User requestId) {
        Optional<FriendRequest> friendRequestOpt = friendRequestRepository.findByRequesterAndRecipient(requestId, currUser);
        if (friendRequestOpt.isPresent()  && friendRequestOpt.get().getStatus().equals(FriendRequest.RequestStatus.PENDING)) {
            friendRequestRepository.delete(friendRequestOpt.get());
        } else {
            throw new EntityNotFoundException("Friend request not found");
        }
    }
}

