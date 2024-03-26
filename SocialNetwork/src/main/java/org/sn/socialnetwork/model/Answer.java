package org.sn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sdp; // Session Description Protocol information

    @ManyToOne
//    @JoinColumn(name = "from_user", referencedColumnName = "id")
    private User fromUser; // Identifier of the sender (callee)

    @ManyToOne
//    @JoinColumn(name = "to_user", referencedColumnName = "id")
    private User toUser; // Identifier of the recipient (caller)
}
