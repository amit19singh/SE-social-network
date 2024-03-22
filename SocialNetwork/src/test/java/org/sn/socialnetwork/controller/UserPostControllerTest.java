package org.sn.socialnetwork.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sn.socialnetwork.dto.UserPostDTO;
import org.springframework.http.MediaType;

import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.UserPost;
import org.sn.socialnetwork.security_and_config.SecurityUtils;
import org.sn.socialnetwork.service.StorageService;
import org.sn.socialnetwork.service.UserPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.mock.web.MockMultipartFile;
//
//
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
//
//

public class UserPostControllerTest {
//
    private UserPostController userPostController;

    @Mock
    private UserPostService userPostService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private StorageService storageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userPostController = new UserPostController(userPostService, securityUtils, storageService);
    }

    @Test
    public void testCreatePost() throws IOException {
        // Arrange
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setCaption("Test Caption");
        userPostDTO.setPost("Test Post");

        User user = new User(); // Create a user object as needed

        // Mocking behavior for securityUtils.getCurrentUser()
        when(securityUtils.getCurrentUser()).thenReturn(user);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        // Mocking behavior for storageService.uploadFile()
        when(storageService.uploadFile(eq(imageFile), anyString())).thenReturn("http://example.com/test.jpg");

        // Act
        ResponseEntity<UserPost> responseEntity = userPostController.createPost(userPostDTO);

        // Assert
        assertEquals(responseEntity.getStatusCodeValue(), 200);
        // Add more assertions as needed
    }
    @Test
    public void testDeletePost_Success() {
        Long postId = 1L;
        UUID userId = UUID.randomUUID();

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(userPostService.deletePost(postId, userId)).thenReturn(true);

        ResponseEntity<?> response = userPostController.deletePost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

//    @Test
//    public void testLikePost_Success() {
//        Long postId = 1L;
//
//        when(userPostService.addLikeToPost(postId)).thenReturn(true);
//
//        ResponseEntity<String> response = userPostController.likePost(postId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Post liked successfully", response.getBody());
//    }
//
    @Test
    public void testUnlikePost_Success() {
        Long postId = 1L;

        when(userPostService.removeLikeFromPost(postId)).thenReturn(true);

        ResponseEntity<String> response = userPostController.unlikePost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Post unliked successfully", response.getBody());
    }
}
