package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
}
