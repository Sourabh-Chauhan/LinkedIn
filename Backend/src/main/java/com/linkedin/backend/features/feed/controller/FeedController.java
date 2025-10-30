package com.linkedin.backend.features.feed.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.linkedin.backend.dto.Response;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.dto.CommentDto;
import com.linkedin.backend.features.feed.model.Comment;
import com.linkedin.backend.features.feed.model.Post;
import com.linkedin.backend.features.feed.service.FeedService;
import com.linkedin.backend.features.feed.service.impl.FeedServiceImpl;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {
    private final FeedService feedService;

    public FeedController(FeedServiceImpl feedService) {
        this.feedService = feedService;
    }

    @GetMapping()
    public ResponseEntity<List<Post>> getFeedPosts(@RequestAttribute("authenticatedUser") User user) {
        return ResponseEntity.ok(feedService.getFeedPosts(user.getId()));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(feedService.getAllPosts());
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestParam(value = "picture", required = false) MultipartFile picture,
            @RequestParam("content") String content,
            @RequestAttribute("authenticatedUser") User user) {
        System.out.println("Creating post with ID: " + picture.getSize());
        Post createdPost = feedService.createPost(picture, content, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {
        Post post = feedService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            @RequestParam("content") String content,
            @RequestAttribute("authenticatedUser") User user) {

        Post updatedPost = feedService.editPost(postId, user.getId(), picture, content);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Response> deletePost(@PathVariable Long postId,
            @RequestAttribute("authenticatedUser") User user) {
        feedService.deletePost(postId, user.getId());
        return ResponseEntity.ok(new Response("Post deleted successfully."));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId, @RequestBody CommentDto content,
            @RequestAttribute("authenticatedUser") User user) {
        System.out.println(
                "Adding comment to post with ID: " + postId + " by user ID: " + user.getId() + " with content: "
                        + content.getContent());
        Comment comment = feedService.addComment(postId, user.getId(), content.getContent());
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<Comment> getComments(@PathVariable Long postId) {
        List<Comment> comments = feedService.getPostComments(postId);
        // return comments.stream()
        // .map(comment -> new CommentDto(comment.getContent()))
        // .collect(Collectors.toList());
        return comments;
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> editComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto,
            @RequestAttribute("authenticatedUser") User user) {
        Comment comment = feedService.editComment(commentId, user.getId(), commentDto.getContent());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Response> deleteComment(@PathVariable Long commentId,
            @RequestAttribute("authenticatedUser") User user) {
        feedService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok(new Response("Comment deleted successfully."));
    }

    @PutMapping("/posts/{postId}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long postId, @RequestAttribute("authenticatedUser") User user) {
        Post post = feedService.likePost(postId, user.getId());
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<Set<User>> getPostLikes(@PathVariable Long postId) {
        Set<User> likes = feedService.getPostLikes(postId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long userId) {
        List<Post> posts = feedService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

}
