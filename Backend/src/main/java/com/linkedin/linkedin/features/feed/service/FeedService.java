package com.linkedin.linkedin.features.feed.service;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.linkedin.linkedin.features.authentication.model.User;
import com.linkedin.linkedin.features.feed.model.Comment;
import com.linkedin.linkedin.features.feed.model.Post;

public interface FeedService {
    public Post createPost(MultipartFile picture, String content, Long id);

    public Post getPostById(Long postId);

    public Post editPost(Long postId, Long id, MultipartFile picture, String content);

    public void deletePost(Long postId, Long userId);

    public Post likePost(Long postId, Long userId);

    public Comment addComment(Long postId, Long userId, String content);

    public Comment editComment(Long postId, Long userId, String newContent);

    public void deleteComment(Long commentId, Long userId);

    public List<Post> getPostsByUserId(Long userId);

    public List<Post> getFeedPosts(Long authenticatedUserId);

    public List<Post> getAllPosts();

    public List<Comment> getPostComments(Long postId);

    public Set<User> getPostLikes(Long postId);
}
