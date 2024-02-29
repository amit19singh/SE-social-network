package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.FriendRequest;
import org.sn.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByRecipientAndStatus(User recipient, FriendRequest.RequestStatus status);
    List<FriendRequest> findByRequesterAndStatus(User requester, FriendRequest.RequestStatus status);
    Optional<FriendRequest> findByRequesterAndRecipient(User requester, User recipient);
    @Query(value = "SELECT user_id FROM public.users WHERE user_id IN " +
            "(SELECT recipient_id FROM friend_request WHERE requester_id = :userId AND status = 'ACCEPTED' " +
            "UNION " +
            "SELECT requester_id FROM friend_request WHERE recipient_id = :userId AND status = 'ACCEPTED')",
            nativeQuery = true)
    List<UUID> findFriendsOfUser(@Param("userId") UUID userID);

    @Query(value = "SELECT fr.requester_id FROM friend_request fr WHERE fr.recipient_id = :userId AND fr.status = 'BLOCKED' " +
            "UNION " +
            "SELECT fr.recipient_id FROM friend_request fr WHERE fr.requester_id = :userId AND fr.status = 'BLOCKED'",
            nativeQuery = true)
    List<UUID> findBlockedUsersIds(@Param("userId") UUID userId);

}

