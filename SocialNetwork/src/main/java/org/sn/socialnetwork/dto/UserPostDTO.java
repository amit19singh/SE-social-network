package org.sn.socialnetwork.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPostDTO {
    private Long postId;
    private String caption;
    private String post;
    private MultipartFile image;
    private MultipartFile video;
    private Set<CommentDTO> comments;
    private Set<LikeDTO> likes;
}

