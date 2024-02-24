package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPostRepository extends JpaRepository<UserPost, Long> {
}
