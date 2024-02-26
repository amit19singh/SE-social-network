package org.sn.socialnetwork.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

}

