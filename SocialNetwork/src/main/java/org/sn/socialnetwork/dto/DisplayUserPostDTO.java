package org.sn.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisplayUserPostDTO {
    private String caption;
    private String post;
//    private String profilePicUrl;
    private String imageUrl;
    private String videoUrl;

}
