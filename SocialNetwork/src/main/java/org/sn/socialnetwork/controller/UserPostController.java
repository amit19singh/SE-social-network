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
    private final UserPostRepository userPostRepository;

//    @PostMapping(value="/upload", consumes = {"multipart/form-data"})
//    public ResponseEntity<UserPost> createPost(
////            @RequestParam("caption") String caption,
//            @RequestParam(value = "image", required = false) MultipartFile image,
//            @RequestParam(value = "video", required = false) MultipartFile video) throws IOException {
//        User user = getUserFromAuth.getCurrentUser();
//        UserPost post = new UserPost();
//        post.setUser(user);
////        post.setCaption(caption);
//        System.out.println("HERE");
//
//        // Handle file upload
//        if (image != null && !image.isEmpty()) {
//            System.out.println("Here 1");
//            String imageUrl = storageService.uploadFile(image, image.getOriginalFilename());
//            post.setImageUrl(imageUrl);
//        }
//        if (video != null && !video.isEmpty()) {
//            System.out.println("Here 2");
//            String videoUrl = storageService.uploadFile(video, video.getOriginalFilename());
//            post.setVideoUrl(videoUrl);
//        }
//
//        UserPost createdPost = userPostService.createPost(post);
//        return ResponseEntity.ok(createdPost);
//    }

    @PostMapping(value="/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<UserPost> createPost(@ModelAttribute UserPostDTO userPostDTO) throws IOException {
        User user = getUserFromAuth.getCurrentUser();
        UserPost post = new UserPost();
        post.setUser(user);
        post.setCaption(userPostDTO.getCaption());
        post.setPost(userPostDTO.getPost());
        System.out.println("HERE");

        // Handle file upload
        if (userPostDTO.getImage() != null && !userPostDTO.getImage().isEmpty()) {
            System.out.println("Here 1");
            String imageUrl = storageService.uploadFile(userPostDTO.getImage(), userPostDTO.getImage().getOriginalFilename());
            post.setImageUrl(imageUrl);
        }
        if (userPostDTO.getVideo() != null && !userPostDTO.getVideo().isEmpty()) {
            System.out.println("Here 2");
            String videoUrl = storageService.uploadFile(userPostDTO.getVideo(), userPostDTO.getVideo().getOriginalFilename());
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


