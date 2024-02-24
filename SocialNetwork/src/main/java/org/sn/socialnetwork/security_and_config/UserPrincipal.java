package org.sn.socialnetwork.security_and_config;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sn.socialnetwork.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails {

    private Collection<? extends GrantedAuthority> authorities;

    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private LocalDate birthday;
    private String gender;
    private boolean isTwoFactorEnabled;
    private boolean verified;
    private String profilePicUrl;
    private String livesIn;
    private String userHometown;
    private String relationshipStatus;
//    private String securityQuestion1;
//    private String securityAnswer1;
//    private String securityQuestion2;
//    private String securityAnswer2;
//    private LocalDateTime createdAt;
//    private String twoFactorSecret;


    public UserPrincipal(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.birthday = user.getBirthday();
        this.gender = user.getGender();
        this.isTwoFactorEnabled = user.isTwoFactorEnabled();
        this.verified = user.isVerified();
        this.profilePicUrl = user.getProfilePicUrl();
        this.livesIn = user.getLivesIn();
        this.userHometown = user.getUserHometown();
        this.relationshipStatus = user.getRelationshipStatus();
//        this.securityQuestion1 = user.getSecurityQuestion1();
//        this.securityAnswer1 = user.getSecurityAnswer1();
//        this.securityQuestion2 = user.getSecurityQuestion2();
//        this.securityAnswer2 = user.getSecurityAnswer2();
//        this.createdAt = user.getCreatedAt();
//        this.twoFactorSecret = user.getTwoFactorSecret();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Here you should return the user's roles. For simplicity, we'll return an empty list.
        // In a real application, you might want to add roles to the User entity and convert them to GrantedAuthority objects.
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // You can implement logic to determine if the user's account is expired.
        // For simplicity, this example returns true.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // You can implement logic to determine if the user's account is locked.
        // For simplicity, this example returns true.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // You can implement logic to determine if the user's credentials (password) are expired.
        // For simplicity, this example returns true.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Here, you might want to check if the user is verified or not.
        // For simplicity, this example returns the value of the verified field.
        return this.verified;
    }

}
