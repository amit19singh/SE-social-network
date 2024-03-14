package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = ?1")
    Boolean CheckUsernameExists(String username);
    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?1")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    List<User> findByIdIn(List<UUID> blockedUserIds);
    
}

