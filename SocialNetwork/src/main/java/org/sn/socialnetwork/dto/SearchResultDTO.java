package org.sn.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {
    private List<UserBasicInfoDTO> users;
    private List<DisplayUserPostDTO> posts;
}
