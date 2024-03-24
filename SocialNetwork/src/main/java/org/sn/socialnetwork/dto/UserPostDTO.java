package org.sn.socialnetwork.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    private List<CommentDTO> comments;
    private List<LikeDTO> likes;
}

