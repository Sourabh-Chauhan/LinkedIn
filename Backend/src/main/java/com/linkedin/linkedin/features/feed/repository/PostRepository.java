package com.linkedin.linkedin.features.feed.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkedin.linkedin.features.feed.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long authorId);

    List<Post> findAllByOrderByCreationDateDesc();

    List<Post> findByAuthorIdInOrderByCreationDateDesc(Set<Long> connectedUserIds);

}
