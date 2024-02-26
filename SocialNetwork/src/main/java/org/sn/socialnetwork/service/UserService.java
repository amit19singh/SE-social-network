package org.sn.socialnetwork.service;


import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.EmailAlreadyInUseException;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.ExceptionHandler.UsernameAlreadyInUseException;
import org.sn.socialnetwork.dto.DisplayUserPostDTO;
import org.sn.socialnetwork.dto.UserDTO;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.UserPost;
import org.sn.socialnetwork.model.VerificationToken;
import org.sn.socialnetwork.model.VerificationToken.TokenType;
import org.sn.socialnetwork.repository.UserPostRepository;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.repository.VerificationTokenRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    final private UserRepository userRepository ;
    final private PasswordEncoder passwordEncoder;
    final private VerificationTokenRepository tokenRepository;
    final private EmailService emailService;
    private final StorageService storageService;
    final private UserPostRepository userPostRepository;

    public User registerUser(User user){

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new EmailAlreadyInUseException("Email already in use. Please use a different email.");

        String username = new ArrayList<>(List.of(user.getEmail().split("@"))).get(0);

        if (userRepository.CheckUsernameExists(username))
            throw new UsernameAlreadyInUseException("Username already taken. Please use different email.");


        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User registeredUser = userRepository.save(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(registeredUser, token, TokenType.REGISTRATION_VERIFICATION);
        tokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendRegisterVerificationEmail(registeredUser, token);

        return registeredUser;
    }

    public String validateVerificationToken(String token, TokenType expectedType) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);

        if (verificationToken.isEmpty() ||
                verificationToken.get().getExpiryDate().isBefore(LocalDateTime.now())||
                verificationToken.get().getType() != expectedType) {
            return "invalid";
        }

        User user = verificationToken.get().getUser();
        if (expectedType == TokenType.REGISTRATION_VERIFICATION) {
            user.setVerified(true);
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        return "valid";
    }

    public User updateUser(UUID id, UserDTO userDTO) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (userDTO.getFirstname() != null && !userDTO.getFirstname().trim().isEmpty()) {
            user.setFirstname(userDTO.getFirstname());
        }
        if (userDTO.getLastname() != null && !userDTO.getLastname().trim().isEmpty()) {
            user.setLastname(userDTO.getLastname());
        }
        if (userDTO.getBirthday() != null) {
            user.setBirthday(userDTO.getBirthday());
        }
        if (userDTO.getGender() != null && !userDTO.getGender().trim().isEmpty()) {
            user.setGender(userDTO.getGender());
        }
        if (userDTO.getProfilePicUrl() != null && !userDTO.getProfilePicUrl().isEmpty()) {
            String profilePicUrl = storageService.uploadFile(userDTO.getProfilePicUrl(),
                    user.getUsername() + "_" + userDTO.getProfilePicUrl().getOriginalFilename());
            user.setProfilePicUrl(profilePicUrl);
        }
        if (userDTO.getLivesIn() != null && !userDTO.getLivesIn().trim().isEmpty()) {
            user.setLivesIn(userDTO.getLivesIn());
        }
        if (userDTO.getUserHometown() != null && !userDTO.getUserHometown().trim().isEmpty()) {
            user.setUserHometown(userDTO.getUserHometown());
        }
        if (userDTO.getRelationshipStatus() != null && !userDTO.getRelationshipStatus().trim().isEmpty()) {
            user.setRelationshipStatus(userDTO.getRelationshipStatus());
        }
        return userRepository.save(user);
    }

    public UserDTO getUserDetailsWithPosts(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<DisplayUserPostDTO> displayUserPostDTOS = userPostRepository.findPostsByUser(user, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::convertToDisplayUserDto)
                .collect(Collectors.toList());

        return UserDTO.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .username(user.getUsername())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .isTwoFactorEnabled(user.isTwoFactorEnabled())
                .livesIn(user.getLivesIn())
                .userHometown(user.getUserHometown())
                .relationshipStatus(user.getRelationshipStatus())
                .posts(displayUserPostDTOS)
                .build();
    }


    private DisplayUserPostDTO convertToDisplayUserDto(UserPost displayUserPostDTO) {
        return DisplayUserPostDTO.builder()
                .postId(displayUserPostDTO.getPostId())
                .caption(displayUserPostDTO.getCaption())
                .post(displayUserPostDTO.getPost())
                .imageUrl(displayUserPostDTO.getImageUrl())
                .videoUrl(displayUserPostDTO.getVideoUrl())
                .build();
    }

}


