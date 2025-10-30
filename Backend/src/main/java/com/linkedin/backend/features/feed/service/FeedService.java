package com.linkedin.backend.features.feed.service;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.model.Comment;
import com.linkedin.backend.features.feed.model.Post;

public interface FeedService {
    Post createPost(MultipartFile picture, String content, Long id);

    Post getPostById(Long postId);

    Post editPost(Long postId, Long id, MultipartFile picture, String content);

    void deletePost(Long postId, Long userId);

    Post likePost(Long postId, Long userId);

    Comment addComment(Long postId, Long userId, String content);

    Comment editComment(Long postId, Long userId, String newContent);

    void deleteComment(Long commentId, Long userId);

    List<Post> getPostsByUserId(Long userId);

    List<Post> getFeedPosts(Long authenticatedUserId);

    List<Post> getAllPosts();

    List<Comment> getPostComments(Long postId);

    Set<User> getPostLikes(Long postId);
}
