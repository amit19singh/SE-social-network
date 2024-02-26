package org.sn.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sn.socialnetwork.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequestDto {
    private String requesterUsername;
    private String recipientUsername;
}
