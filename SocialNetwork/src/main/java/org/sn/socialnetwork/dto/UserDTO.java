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
public class UserDTO {
    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private LocalDate birthday;
    private String gender;
    private boolean isTwoFactorEnabled;
    private MultipartFile profilePicUrl;
    private String profilePicUrlDisplay;
    private String requestSent;
    private String livesIn;
    private String userHometown;
    private String relationshipStatus;
    private List<DisplayUserPostDTO> posts;
//    private List<User> friendRequestsPending;
//    private List<User> friends;
    private List<UserBasicInfoDTO> friendRequestsPending;
    private List<UserBasicInfoDTO> friends;
}


