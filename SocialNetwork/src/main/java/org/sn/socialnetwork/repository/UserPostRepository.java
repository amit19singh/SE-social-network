package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.User;
import org.sn.socialnetwork.model.UserPost;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPostRepository extends JpaRepository<UserPost, Long> {
//    @Query("SELECT u FROM UserPost u WHERE u.user = ?1")
    List<UserPost> findPostsByUser(User user, Sort sort);

    List<UserPost> findByPostContainingIgnoreCase(String query, Sort sort);
}
