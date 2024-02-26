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
        return userPostRepository.findById(postId)
                .filter(post -> post.getUser().getId().equals(userId))
                .map(post -> {
                    userPostRepository.deleteById(postId);
                    return true;
                })
                .orElse(false);
    }
}

