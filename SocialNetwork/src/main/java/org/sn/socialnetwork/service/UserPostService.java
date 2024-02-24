package org.sn.socialnetwork.service;


import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.UserPost;
import org.sn.socialnetwork.repository.UserPostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserPostService {

    final private UserPostRepository userPostRepository;

    public UserPost createPost(UserPost post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return userPostRepository.save(post);
    }

    public boolean deletePost(Long postId, UUID userId) {
        Optional<UserPost> postOptional = userPostRepository.findById(postId);
        if (postOptional.isPresent() && postOptional.get().getUser().getId().equals(userId)) {
            userPostRepository.deleteById(postId);
            return true;
        }
        return false;
    }
}


