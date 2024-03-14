package org.sn.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisplayUserPostDTO {
    private Long postId;
    private String caption;
    private String post;
    private LocalDateTime createdAt;
    private String imageUrl;
    private String videoUrl;
    private List<CommentDTO> comments;
    private List<LikeDTO> likes;

}
