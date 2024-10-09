package com.example.backend.service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Like;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserService userService;

    public LikeService(LikeRepository likeRepository, PostService postService, UserService userService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
        this.userService = userService;
    }

    // Create a like
    public Like createLike(Long userId, Long postId) throws ResourceNotFoundException {
        Optional<User> user = userService.getUserById(userId);
        Optional<Post> post = postService.getPostById(postId);

        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User with id " + userId + " not found.");
        }
        if (!post.isPresent()) {
            throw new ResourceNotFoundException("Post with id " + postId + " not found.");
        }

        Like newLike = new Like();
        newLike.setUser(user.get());
        newLike.setPost(post.get());

        return likeRepository.save(newLike);
    }

    // Get all likes
    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    // Get a like by ID
    public Optional<Like> getLikeById(Long likeId) {
        return likeRepository.findById(likeId);
    }
    public Optional<Like> getLikeByPostIdAndUserId(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId);
    }

    // Delete a like
    public void deleteLike(Long likeId) throws ResourceNotFoundException {
        Optional<Like> existingLike = likeRepository.findById(likeId);

        if (!existingLike.isPresent()) {
            throw new ResourceNotFoundException("Like with id " + likeId + " not found.");
        }

        likeRepository.deleteById(likeId);
    }

    // Count likes by post ID
    public int countLikesByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }
}
