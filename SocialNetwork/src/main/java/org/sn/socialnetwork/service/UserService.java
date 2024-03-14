package org.sn.socialnetwork.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.sn.socialnetwork.ExceptionHandler.EmailAlreadyInUseException;
import org.sn.socialnetwork.ExceptionHandler.UserNotFoundException;
import org.sn.socialnetwork.ExceptionHandler.UsernameAlreadyInUseException;
import org.sn.socialnetwork.dto.*;
import org.sn.socialnetwork.model.*;
import org.sn.socialnetwork.model.VerificationToken.TokenType;
import org.sn.socialnetwork.repository.*;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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
    final private FriendRequestRepository friendRequestRepository;
    final private SecurityUtils securityUtils;
    final private CommentRepository commentRepository;
    @PersistenceContext
    private EntityManager entityManager;

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

    public UserDTO getUserDetailsWithPostsAndFriends(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<DisplayUserPostDTO> displayUserPostDTOS = userPostRepository.findPostsByUser(user,
                                                            Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::convertToDisplayUserDto)
                .collect(Collectors.toList());

        List<UserBasicInfoDTO> friendRequestsPending = friendRequestRepository.findByRecipientAndStatus(user, FriendRequest.RequestStatus.PENDING)
                .stream()
                .map(FriendRequest::getRequester)
                .map(this::convertToUserBasicInfoDto) // You'll need to implement this method
                .collect(Collectors.toList());

        List<UserBasicInfoDTO> friends = friendRequestRepository.findFriendsOfUser(userId).stream()
                .map(id -> entityManager.find(User.class, id))
                .map(this::convertToUserBasicInfoDto)
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .username(user.getUsername())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .isTwoFactorEnabled(user.isTwoFactorEnabled())
                .profilePicUrlDisplay(user.getProfilePicUrl())
                .livesIn(user.getLivesIn())
                .userHometown(user.getUserHometown())
                .relationshipStatus(user.getRelationshipStatus())
                .posts(displayUserPostDTOS)
                .friendRequestsPending(friendRequestsPending)
                .friends(friends)
                .build();
    }

    public SearchResultDTO searchUsersWithCriteriaAPI(String query) {
        SearchResultDTO result = new SearchResultDTO();

//        Search Names and Stuff
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            String[] terms = query.split("\\s+");
            for (String term : terms) {
                String pattern = "%" + term.toLowerCase() + "%";
                Predicate usernamePredicate = cb.like(cb.lower(user.get("username")), pattern);
                Predicate firstnamePredicate = cb.like(cb.lower(user.get("firstname")), pattern);
                Predicate lastnamePredicate = cb.like(cb.lower(user.get("lastname")), pattern);
                predicates.add(cb.or(usernamePredicate, firstnamePredicate, lastnamePredicate));
            }
        }

        UUID currentUserId = securityUtils.getCurrentUser().getId();
        List<UUID> blockedUsersIds = friendRequestRepository.findBlockedUsersIds(currentUserId);
        if (!blockedUsersIds.isEmpty()) {
            Predicate notBlockedPredicate = cb.not(user.get("id").in(blockedUsersIds));
            predicates.add(notBlockedPredicate);
        }

        Predicate notCurrentUserPredicate = cb.notEqual(user.get("id"), currentUserId);
        predicates.add(notCurrentUserPredicate);

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        List<User> users = entityManager.createQuery(cq).getResultList();
        List<UserDTO> userDTOS = users.stream()
                                    .map(this::convertToUserDTO)
                                    .toList();



        result.setUsers(userDTOS);

//        Search Posts
        List<UserPost> matchingPosts = userPostRepository.findByPostContainingIgnoreCase(query,
                                                Sort.by(Sort.Direction.DESC, "createdAt"));
        List<DisplayUserPostDTO> displayUserPostDTOS = matchingPosts.stream()
                                                        .map(this::convertToDisplayUserDto)
                                                        .collect(Collectors.toList());
        result.setPosts(displayUserPostDTOS);

        return result;
    }

    public void updateProfileVisibility(String username, boolean isProfilePublic) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        user.setProfilePublic(isProfilePublic);
        userRepository.save(user);
    }

    public UserDTO getUserProfileForViewer(String ownerUserName) {
        User owner = userRepository.findByUsername(ownerUserName)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + ownerUserName));

        UUID viewerId = securityUtils.getCurrentUser().getId();

        boolean isFriend = friendRequestRepository.findFriendsOfUser(viewerId).stream()
                .anyMatch(friendId -> friendId.equals(owner.getId()));

        if (viewerId.equals(owner.getId()) || isFriend || owner.isProfilePublic()) {
            return buildDetailedProfile(owner.getId());
        } else {
            return buildLimitedProfile(owner.getId());
        }
    }

    private UserDTO buildDetailedProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<DisplayUserPostDTO> displayUserPostDTOS = userPostRepository.findPostsByUser(user, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::convertToDisplayUserDto)
                .collect(Collectors.toList());

        List<UserBasicInfoDTO> friends = friendRequestRepository.findFriendsOfUser(userId).stream()
                .map(id -> entityManager.find(User.class, id))
                .map(this::convertToUserBasicInfoDto)
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .username(user.getUsername())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .livesIn(user.getLivesIn())
                .userHometown(user.getUserHometown())
                .relationshipStatus(user.getRelationshipStatus())
                .posts(displayUserPostDTOS)
                .friends(friends)
                .build();
    }

    private UserDTO buildLimitedProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        System.out.println("LIMITED PROFILE: " + user.getBirthday());

        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .username(user.getUsername())
                .gender(user.getGender())
                .build();
    }

    public List<UserDTO> getBlockedUsers() {
        UUID currentUserId = securityUtils.getCurrentUser().getId();
        List<UUID> blockedUserIds = friendRequestRepository.findBlockedUsersIds(currentUserId);

        List<User> blockedUsers = userRepository.findByIdIn(blockedUserIds);

        return blockedUsers.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }


    private DisplayUserPostDTO convertToDisplayUserDto(UserPost userPost) {
        List<Comment> comments = commentRepository.findByPost(userPost);

        return DisplayUserPostDTO.builder()
                .postId(userPost.getPostId())
                .caption(userPost.getCaption())
                .createdAt(userPost.getCreatedAt())
                .post(userPost.getPost())
                .imageUrl(userPost.getImageUrl())
                .videoUrl(userPost.getVideoUrl())
                .comments(convertToCommentDTO(comments))
                .build();
    }

    private List<CommentDTO> convertToCommentDTO(List<Comment> comments){

        List<CommentDTO> commentDTOS = new ArrayList<>();
        for (Comment comment: comments) {
            commentDTOS.add(CommentDTO.builder()
//                    .postId(comment.getCommentId())
                    .commentText(comment.getCommentText())
                    .createdAt(comment.getCreatedAt())
                    .user(comment.getUser())
                    .build());
        }
        return commentDTOS;
    }

    private UserDTO convertToUserDTO(User user) {
        String requestStatus = "NONE";
        Optional<FriendRequest> friendRequest = friendRequestRepository.findByRequesterAndRecipient(securityUtils.getCurrentUser(), user);

        if (friendRequest.isPresent())
            requestStatus = friendRequest.get().getStatus().toString();

        Optional<FriendRequest> receivedRequest = friendRequestRepository.findByRequesterAndRecipient(user, securityUtils.getCurrentUser());
        if (receivedRequest.isPresent() && receivedRequest.get().getStatus() == FriendRequest.RequestStatus.ACCEPTED)
            requestStatus = receivedRequest.get().getStatus().toString();


        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .username(user.getUsername())
                .requestSent(requestStatus)
                .build();
    }
    private UserBasicInfoDTO convertToUserBasicInfoDto(User user) {
        return UserBasicInfoDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .username(user.getUsername())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .livesIn(user.getLivesIn())
                .userHometown(user.getUserHometown())
                .relationshipStatus(user.getRelationshipStatus())
                .build();
    }



}

