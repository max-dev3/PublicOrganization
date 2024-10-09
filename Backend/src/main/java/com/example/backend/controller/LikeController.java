package com.example.backend.controller;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Like;
import com.example.backend.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // Get all likes
    @GetMapping
    public ResponseEntity<List<Like>> getAllLikes() {
        return ResponseEntity.ok(likeService.getAllLikes());
    }

    // Get a like by ID
    @GetMapping("/{id}")
    public ResponseEntity<Like> getLikeById(@PathVariable Long id) throws ResourceNotFoundException {
        Like like = likeService.getLikeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Like with id " + id + " not found."));
        return ResponseEntity.ok(like);
    }

    // Create a like
    @PostMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<Like> createLike(@PathVariable Long userId, @PathVariable Long postId) throws ResourceNotFoundException {
        Like newLike = likeService.createLike(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newLike);
    }

    // Delete a like
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) throws ResourceNotFoundException {
        likeService.deleteLike(id);
        return ResponseEntity.noContent().build();
    }

    // Count likes for a post
    @GetMapping("/count/post/{postId}")
    public ResponseEntity<Integer> countLikesByPostId(@PathVariable Long postId) {
        int count = likeService.countLikesByPostId(postId);
        return ResponseEntity.ok(count);
    }



}
