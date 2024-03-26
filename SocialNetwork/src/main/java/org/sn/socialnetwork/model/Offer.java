package org.sn.socialnetwork.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;

    @ManyToOne
//    @JoinColumn(name = "from_user", referencedColumnName = "id")
    private User fromUser;

    @ManyToOne
//    @JoinColumn(name = "to_user", referencedColumnName = "id")
    private User toUser;

    private String sdp;
}


