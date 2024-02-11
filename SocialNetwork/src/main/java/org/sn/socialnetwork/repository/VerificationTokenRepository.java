package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUserAndType(User user, VerificationToken.TokenType tokenType);
}
