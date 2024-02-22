package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.UserPost;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.sn.socialnetwork.service.UserPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-posts")
@AllArgsConstructor
public class UserPostController {

    final private UserPostService userPostService;
    final private SecurityUtils getUserFromAuth;

    @PostMapping
    public ResponseEntity<UserPost> createPost(@RequestBody UserPost post) {
        User user = getUserFromAuth.getCurrentUser();
        post.setUser(user);
        UserPost createdPost = userPostService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable UUID postId) {
        UUID userId = getUserFromAuth.getCurrentUserId();
        boolean isDeleted = userPostService.deletePost(postId, userId);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}