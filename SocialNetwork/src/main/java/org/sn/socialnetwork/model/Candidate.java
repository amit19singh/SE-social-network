package org.sn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "candidate")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidate; // The ICE candidate string
    private String sdpMid; // The media stream identification, for which the candidate is associated
    private int sdpMLineIndex; // The index (zero-based) of the m-line in the SDP, for which the candidate is associated

    @ManyToOne
//    @JoinColumn(name = "from_user", referencedColumnName = "id")
    private User fromUser; // Identifier of the sender

    @ManyToOne
//    @JoinColumn(name = "to_user", referencedColumnName = "id")
    private User toUser; // Identifier of the recipient
}
