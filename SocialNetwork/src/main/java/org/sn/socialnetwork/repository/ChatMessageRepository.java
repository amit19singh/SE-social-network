package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
            "(cm.senderUsername = :senderUsername AND cm.receiverUsername = :receiverUsername) OR " +
            "(cm.senderUsername = :receiverUsername AND cm.receiverUsername = :senderUsername) " +
            "ORDER BY cm.timestamp ASC")
    List<ChatMessage> findChatHistory(String senderUsername, String receiverUsername);

    @Query(value = "WITH UniqueUsernames AS (" +
            "SELECT sender_username AS username FROM chat_message " +
            "UNION " +
            "SELECT receiver_username FROM chat_message), " +
            "LatestTimestamps AS (" +
            "SELECT " +
            "username, " +
            "MAX(CASE WHEN sender_username = username THEN timestamp END) AS latest_sent, " +
            "MAX(CASE WHEN receiver_username = username THEN timestamp END) AS latest_received " +
            "FROM chat_message, UniqueUsernames " +
            "WHERE sender_username = username OR receiver_username = username " +
            "GROUP BY username) " +
            "SELECT username, GREATEST(MAX(latest_sent), MAX(latest_received)) AS latest_activity " +
            "FROM LatestTimestamps " +
            "GROUP BY username " +
            "ORDER BY latest_activity", nativeQuery = true)
    List<Object[]> findAllUniqueUsernamesOrderedByLatestMessage();

}
