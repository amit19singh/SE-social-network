package org.sn.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private UUID id;
    private String firstname;
    private String lastname;
    private String username;
    private String gender;
}