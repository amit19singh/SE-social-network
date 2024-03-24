package org.sn.socialnetwork.repository;

import org.sn.socialnetwork.model.Comment;
import org.sn.socialnetwork.model.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(UserPost userPost);
}
