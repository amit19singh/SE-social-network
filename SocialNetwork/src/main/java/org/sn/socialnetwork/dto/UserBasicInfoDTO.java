package org.sn.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBasicInfoDTO {
    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private LocalDate birthday;
    private String gender;
    private MultipartFile profilePicUrl;
    private String requestSent;
    private String livesIn;
    private String userHometown;
    private String relationshipStatus;
    private List<DisplayUserPostDTO> posts;
    private List<UserBasicInfoDTO> friends;
}
