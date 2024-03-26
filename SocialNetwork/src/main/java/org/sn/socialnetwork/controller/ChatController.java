package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.dto.AnswerDTO;
import org.sn.socialnetwork.dto.CandidateDTO;
import org.sn.socialnetwork.dto.OfferDTO;
import org.sn.socialnetwork.model.ChatMessage;
import org.sn.socialnetwork.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    final private ChatService chatService;

    @PostMapping("/messages/send")
    public void sendMessage(@RequestBody ChatMessage chatMessage) {
        chatService.sendMessage(chatMessage);
    }

    @MessageMapping("/messages/send")
    public void receiveMessage(@Payload ChatMessage chatMessage) {
        // Handle the received message here
        // For example, save it to the database and/or forward it to the recipient
    }

//    @PostMapping("/offer")
//    public ResponseEntity<?> handleOffer(@RequestBody OfferDTO offerDTO) {
//        chatService.processOffer(offerDTO);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/answer")
//    public ResponseEntity<?> handleAnswer(@RequestBody AnswerDTO answerDTO) {
//        chatService.processAnswer(answerDTO);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/candidate")
//    public ResponseEntity<?> handleIceCandidate(@RequestBody CandidateDTO candidateDTO) {
//        chatService.addIceCandidate(candidateDTO);
//        return ResponseEntity.ok().build();
//    }

}
