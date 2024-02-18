package org.sn.socialnetwork.dto;

import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    private String token;
    private String newPassword;
}
