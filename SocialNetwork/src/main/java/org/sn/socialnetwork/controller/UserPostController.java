package org.sn.socialnetwork.controller;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.dto.UserPostDTO;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.UserPost;
import org.sn.socialnetwork.repository.UserPostRepository;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.sn.socialnetwork.service.StorageService;
import org.sn.socialnetwork.service.UserPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-posts")
@AllArgsConstructor
public class UserPostController {

    final private UserPostService userPostService;
    final private SecurityUtils getUserFromAuth;
    private final StorageService storageService;

    @PostMapping(value="/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<UserPost> createPost(@ModelAttribute UserPostDTO userPostDTO) throws IOException {
        User user = getUserFromAuth.getCurrentUser();
        UserPost post = new UserPost();
        post.setUser(user);
        post.setCaption(userPostDTO.getCaption());
        post.setPost(userPostDTO.getPost());

        // Handle file upload and set fileName: Username + OriginalFileName
        if (userPostDTO.getImage() != null && !userPostDTO.getImage().isEmpty()) {
            String imageUrl = storageService.uploadFile(userPostDTO.getImage(),
                    user.getUsername() + "_" + userPostDTO.getImage().getOriginalFilename(), "images/");
            post.setImageUrl(imageUrl);
        }
        if (userPostDTO.getVideo() != null && !userPostDTO.getVideo().isEmpty()) {
            String videoUrl = storageService.uploadFile(userPostDTO.getVideo(),
                    user.getUsername() + "_" + userPostDTO.getVideo().getOriginalFilename(), "videos/");
            post.setVideoUrl(videoUrl);
        }

        UserPost createdPost = userPostService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        UUID userId = getUserFromAuth.getCurrentUserId();
        boolean isDeleted = userPostService.deletePost(postId, userId);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}


