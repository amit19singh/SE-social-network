package org.sn.socialnetwork.service;


import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.Like;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.UserPost;
import org.sn.socialnetwork.repository.LikeRepository;
import org.sn.socialnetwork.repository.UserPostRepository;
import org.sn.socialnetwork.repository.UserRepository;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.google.cloud.storage.Storage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
//@AllArgsConstructor
public class UserPostService {
    final private UserPostRepository userPostRepository;
    final private Storage storage;
    final private String bucketName;
    final private SecurityUtils securityUtils;
    final private LikeRepository likeRepository;

    public UserPostService(Storage storage, @Value("${google.cloud.storage.bucket-name}") String bucketName,
                           UserPostRepository userPostRepository, SecurityUtils securityUtils, LikeRepository likeRepository) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.userPostRepository = userPostRepository;
        this.securityUtils = securityUtils;
        this.likeRepository = likeRepository;
    }

    public UserPost createPost(UserPost post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return userPostRepository.save(post);
    }


    public boolean deletePost(Long postId, UUID userId) {
        Optional<UserPost> postOptional = userPostRepository.findById(postId);
        if (postOptional.isEmpty()) return false;

        UserPost post = postOptional.get();
        if (!post.getUser().getId().equals(userId))
            throw new AccessDeniedException("User not authorized to delete this post.");

        deleteFileFromCloudStorage(post.getImageUrl());
        deleteFileFromCloudStorage(post.getVideoUrl());

        userPostRepository.deleteById(postId);
        return true;
    }

    private void deleteFileFromCloudStorage(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty())
            return;
        String fileName = extractFileName(fileUrl);
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
    }

    private String extractFileName(String fileUrl) {
        String path = fileUrl.split("\\?")[0];
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        return java.net.URLDecoder.decode(fileName, StandardCharsets.UTF_8);
    }


    public boolean addLikeToPost(Long postId) throws Exception {
        UserPost post = userPostRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        User user = securityUtils.getCurrentUser();

        boolean isAlreadyLiked = likeRepository.existsByPostAndUser(post, user);
        if (!isAlreadyLiked) {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            like.setCreatedAt(LocalDateTime.now());
            likeRepository.save(like);
            return true;
        }
        return false;
    }

    public boolean removeLikeFromPost(Long postId) {
        UserPost post = userPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = securityUtils.getCurrentUser();

        Optional<Like> likeOptional = likeRepository.findByPostAndUser(post, user);
        System.out.println("likeOptional: " + likeOptional);

        if (likeOptional.isPresent()) {
            likeRepository.delete(likeOptional.get());
            return true;
        } else {
            throw new IllegalArgumentException("Like not found for the given post and user");
        }
    }
}
