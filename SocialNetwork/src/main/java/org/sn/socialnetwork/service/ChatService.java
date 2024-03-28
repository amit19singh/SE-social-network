package org.sn.socialnetwork.service;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.dto.AnswerDTO;
import org.sn.socialnetwork.dto.CandidateDTO;
import org.sn.socialnetwork.dto.OfferDTO;
import org.sn.socialnetwork.model.*;
import org.sn.socialnetwork.repository.*;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {

    final private ChatMessageRepository chatMessageRepository;
    final private SimpMessagingTemplate messagingTemplate;
    final private SecurityUtils securityUtils;

    public void receiveMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverUsername(),
                "/private",
                chatMessage);
    }

    public List<ChatMessage> fetchChatHistory(String senderUsername, String receiverUsername) {
        return chatMessageRepository.findChatHistory(senderUsername, receiverUsername);
    }


    public List<String> fetchAllChatHistory() {
        List<Object[]> results = chatMessageRepository.findAllUniqueUsernamesOrderedByLatestMessage();
        List<String> list = results.stream()
                .map(result -> (String) result[0])
                .filter(username -> !username.equals(securityUtils.getCurrentUser().getUsername()))
                .toList();
//                .collect(Collectors.toList());
        System.out.println("list: " + list);
        return list;
    }
}
