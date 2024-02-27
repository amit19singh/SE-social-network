package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.FriendRequest;
import org.sn.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByRecipientAndStatus(User recipient, FriendRequest.RequestStatus status);
    List<FriendRequest> findByRequesterAndStatus(User requester, FriendRequest.RequestStatus status);
    Optional<FriendRequest> findByRequesterAndRecipient(User requester, User recipient);


}

