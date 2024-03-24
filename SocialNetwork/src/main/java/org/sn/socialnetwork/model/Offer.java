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

    private String fromUserId;
    private String toUserId;
    private String sdp;
}


