package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.Like;
import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostAndUser(UserPost post, User user);

    Optional<Like> findByPostAndUser(UserPost post, User user);

    List<Like> findByPost(UserPost post);
}

