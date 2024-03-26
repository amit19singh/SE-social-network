package org.sn.socialnetwork.service;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.dto.AnswerDTO;
import org.sn.socialnetwork.dto.CandidateDTO;
import org.sn.socialnetwork.dto.OfferDTO;
import org.sn.socialnetwork.model.*;
import org.sn.socialnetwork.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ChatService {

    final private ChatMessageRepository chatMessageRepository;
    final private SimpMessagingTemplate messagingTemplate;

    public void sendMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(),
                "/queue/messages",
                chatMessage);
    }

//    final private OfferRepository offerRepository;
//    final private AnswerRepository answerRepository;
//    final private CandidateRepository candidateRepository;
//    final private UserRepository userRepository;
//    public void processOffer(OfferDTO offerDTO) {
//        UUID fromUserId = UUID.fromString(offerDTO.getFromUserId());
//        UUID toUserId = UUID.fromString(offerDTO.getToUserId());
//
//        User fromUser = userRepository.findById(fromUserId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + fromUserId));
//        User toUser = userRepository.findById(toUserId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + toUserId));
//
//        Offer offer = new Offer();
//        offer.setFromUser(fromUser);
//        offer.setToUser(toUser);
//        offer.setSdp(offerDTO.getSdp());
//        offerRepository.save(offer);
//    }
//
//
//    public void processAnswer(AnswerDTO answerDTO) {
//        UUID fromUserId = UUID.fromString(answerDTO.getFromUserId());
//        UUID toUserId = UUID.fromString(answerDTO.getToUserId());
//
//        User fromUser = userRepository.findById(fromUserId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + fromUserId));
//        User toUser = userRepository.findById(toUserId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + toUserId));
//
//        Answer answer = new Answer();
//        answer.setFromUser(fromUser); // Assuming Answer model is adjusted for User relationships
//        answer.setToUser(toUser); // Assuming Answer model is adjusted for User relationships
//        answer.setSdp(answerDTO.getSdp());
//        answerRepository.save(answer);
//    }
//
//    public void addIceCandidate(CandidateDTO candidateDTO) {
//        UUID fromUserId = UUID.fromString(candidateDTO.getFromUserId());
//        UUID toUserId = UUID.fromString(candidateDTO.getToUserId());
//
//        User fromUser = userRepository.findById(fromUserId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + fromUserId));
//        User toUser = userRepository.findById(toUserId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + toUserId));
//
//        Candidate candidate = new Candidate();
//        candidate.setFromUser(fromUser); // Assuming Candidate model is adjusted for User relationships
//        candidate.setToUser(toUser); // Assuming Candidate model is adjusted for User relationships
//        candidate.setCandidate(candidateDTO.getCandidate());
//        candidate.setSdpMid(candidateDTO.getSdpMid());
//        candidate.setSdpMLineIndex(candidateDTO.getSdpMLineIndex());
//        candidateRepository.save(candidate);
//    }
}
