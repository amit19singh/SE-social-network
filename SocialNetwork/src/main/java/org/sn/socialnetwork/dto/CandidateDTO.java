package org.sn.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDTO {
    private String candidate; // The ICE candidate string
    private String sdpMid; // The media stream identification, for which the candidate is associated
    private int sdpMLineIndex; // The index (zero-based) of the m-line in the SDP, for which the candidate is associated
    private String fromUserId; // Identifier of the sender
    private String toUserId; // Identifier of the recipient
}

