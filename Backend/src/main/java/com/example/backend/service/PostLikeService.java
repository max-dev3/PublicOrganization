package com.example.backend.service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Like;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import org.springframework.stereotype.Service;

@Service
public class PostLikeService {

    private final PostService postService;
    private final LikeService likeService;

    public PostLikeService(PostService postService, LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    public Post toggleLike(Long postId, Long userId) throws ResourceNotFoundException {
        Post post = postService.getPostById(postId).orElseThrow(() -> new ResourceNotFoundException("Post with id " + postId + " not found."));
        Like like = likeService.getLikeByPostIdAndUserId(postId, userId).orElse(null);
        if (like == null) {
            // If the user hasn't liked this post yet, create a new like
            likeService.createLike(userId, postId);
        } else {
            // If the user has already liked this post, delete the like
            likeService.deleteLike(like.getId());
        }
        return post;
    }

}
