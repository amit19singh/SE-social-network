package org.sn.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    private String sdp; // Session Description Protocol information
    private String fromUserId; // Identifier of the sender (callee)
    private String toUserId; // Identifier of the recipient (caller)
}
