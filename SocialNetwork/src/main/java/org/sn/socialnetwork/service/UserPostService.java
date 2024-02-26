package org.sn.socialnetwork.service;


import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.model.UserPost;
import org.sn.socialnetwork.repository.UserPostRepository;
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

    public UserPostService(Storage storage, @Value("${google.cloud.storage.bucket-name}") String bucketName, UserPostRepository userPostRepository) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.userPostRepository = userPostRepository;
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

}
