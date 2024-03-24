package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.dto.AnswerDTO;
import org.sn.socialnetwork.dto.CandidateDTO;
import org.sn.socialnetwork.dto.OfferDTO;
import org.sn.socialnetwork.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    final private ChatService chatService;

    @PostMapping("/offer")
    public ResponseEntity<?> handleOffer(@RequestBody OfferDTO offerDTO) {
        chatService.processOffer(offerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answer")
    public ResponseEntity<?> handleAnswer(@RequestBody AnswerDTO answerDTO) {
        chatService.processAnswer(answerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/candidate")
    public ResponseEntity<?> handleIceCandidate(@RequestBody CandidateDTO candidateDTO) {
        chatService.addIceCandidate(candidateDTO);
        return ResponseEntity.ok().build();
    }

}
