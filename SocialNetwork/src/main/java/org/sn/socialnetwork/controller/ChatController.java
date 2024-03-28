package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.dto.AnswerDTO;
import org.sn.socialnetwork.dto.CandidateDTO;
import org.sn.socialnetwork.dto.OfferDTO;
import org.sn.socialnetwork.model.ChatMessage;
import org.sn.socialnetwork.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/api/chat")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    final private ChatService chatService;

    @MessageMapping("/private-message")
    public ChatMessage receivePrivateMessage(@Payload ChatMessage chatMessage) {
        chatService.receiveMessage(chatMessage);
        System.out.println("Message: " + chatMessage.toString());
        return chatMessage;
    }

    @GetMapping("/chat/history/{userChat}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String userChat) {
        System.out.println("In Controller");
        List<ChatMessage> chatHistory = chatService.fetchChatHistory(userChat);
        return ResponseEntity.ok(chatHistory);
    }

    @GetMapping("/chat/history/all")
    public ResponseEntity<List<String>> getAllChatHistory() {
        List<String> chatHistory = chatService.fetchAllChatHistory();
        return ResponseEntity.ok(chatHistory);
    }


}
