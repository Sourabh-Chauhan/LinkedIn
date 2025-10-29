package com.linkedin.linkedin.features.feed.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.linkedin.linkedin.features.authentication.model.User;
import com.linkedin.linkedin.features.authentication.repository.UserRepository;
import com.linkedin.linkedin.features.feed.model.Comment;
import com.linkedin.linkedin.features.feed.model.Post;
import com.linkedin.linkedin.features.feed.repository.CommentRepository;
import com.linkedin.linkedin.features.feed.repository.PostRepository;
import com.linkedin.linkedin.features.feed.service.FeedService;

@Service
public class FeedServiceImpl implements FeedService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // Inject any required dependencies here
    public FeedServiceImpl(UserRepository userRepository, CommentRepository commentRepository,
            PostRepository postRepository) {
        // Initialize dependencies here
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Post createPost(MultipartFile picture, String content, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create a new Post entity
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        String pictureURL = picture.getOriginalFilename();
        // String pictureUrl = storageService.saveImage(picture);
        post.setPicture(pictureURL);

        return postRepository.save(post);
    }

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    @Override
    public Post editPost(Long postId, Long userId, MultipartFile picture, String content) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("User is not the author of the post");
        }

        post.setContent(content);

        String pictureURL = picture.getOriginalFilename();
        // String pictureUrl = storageService.saveImage(picture);
        post.setPicture(pictureURL);
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("User is not the author of the post");
        }
        postRepository.delete(post);
    }

    @Override
    public Post likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<User> likes = post.getLikes();
        if (likes.contains(user)) {
            likes.remove(user); // Unlike the post
            post.setLikeCount(post.getLikeCount() - 1);
            System.out.println("Post now has " + post.getLikeCount() + " likes.");
        } else {
            likes.add(user); // Like the post
            post.setLikeCount(post.getLikeCount() + 1);
            System.out.println("Post now has " + post.getLikeCount() + " likes.");
        }
        post.setLikes(likes);

        return postRepository.save(post);
    }

    @Override
    public Comment addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment newComment = new Comment();
        newComment.setPost(post);
        newComment.setAuthor(user);
        newComment.setContent(content);
        return commentRepository.save(newComment);
    }

    @Override
    public Comment editComment(Long commentId, Long userId, String newContent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User is not the author of the comment");
        }

        comment.setContent(newContent);
        return commentRepository.save(comment);

    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User is not the author of the comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByAuthorId(userId);
    }

    @Override
    public List<Post> getFeedPosts(Long authenticatedUserId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFeedPosts'");
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreationDateDesc();
    }

    @Override
    public List<Comment> getPostComments(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return post.getComments();
    }

    @Override
    public Set<User> getPostLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return post.getLikes();

    }
}
