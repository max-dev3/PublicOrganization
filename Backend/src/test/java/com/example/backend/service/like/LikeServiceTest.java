package com.example.backend.service.like;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Like;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.LikeRepository;
import com.example.backend.service.EntityBuilder;
import com.example.backend.service.LikeService;
import com.example.backend.service.PostService;
import com.example.backend.service.UserService;


public class LikeServiceTest extends EntityBuilder {
  
    @Mock private LikeRepository likeRepository;
    @Mock private PostService postService;
    @Mock private UserService userService;

    private LikeService likeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        likeService = new LikeService(likeRepository, postService, userService);
    }

    @Test
    void createLikeTest() {
        User user = getUserEntity();
        Post post = getPostEntity();
        
        when(userService.getUserById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(postService.getPostById(post.getId())).thenReturn(java.util.Optional.of(post));
        when(likeRepository.save( any(Like.class)) ).thenAnswer( getPassedObjectAnswer() );

        Like result = assertDoesNotThrow( () -> likeService.createLike(user.getId(), post.getId()) );

        verify(likeRepository, times(1)).save( any(Like.class) );
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(post.getId(), result.getPost().getId());
    }

    @Test
    void createLikeTest_userNotFound() {
        User user = getUserEntity();
        Post post = getPostEntity();
        
        when(userService.getUserById(user.getId())).thenReturn(java.util.Optional.empty());
        when(postService.getPostById(post.getId())).thenReturn(java.util.Optional.of(post));

        assertThrows(ResourceNotFoundException.class, () -> likeService.createLike(user.getId(), post.getId()));
    }

    @Test
    void createLikeTest_postNotFound() {
        User user = getUserEntity();
        Post post = getPostEntity();
        
        when(userService.getUserById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(postService.getPostById(post.getId())).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> likeService.createLike(user.getId(), post.getId()));
    }

    @Test
    void getAllLikesTest() {
        var likeList = List.of( getLikeEntity(), getLikeEntity() );
        when( likeRepository.findAll() ).thenReturn( likeList );
        
        var result = likeService.getAllLikes();

        verify( likeRepository, times(1) ).findAll();
        assertEquals(2, result.size());
        assertListEquals(likeList, result);
    }

    @Test
    void getLikeByIdTest() {
        var like = getLikeEntity();
        when( likeRepository.findById(like.getId()) ).thenReturn( java.util.Optional.of(like) );
        
        var result = likeService.getLikeById(like.getId());

        verify( likeRepository, times(1) ).findById(like.getId());
        assertEquals(like.getId(), result.get().getId());
    }

    @Test
    void getLikeByPostIdAndUserIdTest() {
        var like = getLikeEntity();
        var post = like.getPost();
        var user = like.getUser();
        when( likeRepository.findByPostIdAndUserId(post.getId(), user.getId()) ).thenReturn( java.util.Optional.of(like) );
        
        var result = likeService.getLikeByPostIdAndUserId(post.getId(), user.getId());

        verify( likeRepository, times(1) ).findByPostIdAndUserId(post.getId(), user.getId());
        assertEquals(like.getId(), result.get().getId());
    }

    @Test
    void deleteLikeTest() {
        var like = getLikeEntity();
        when( likeRepository.findById(like.getId()) ).thenReturn( java.util.Optional.of(like) );
        
        assertDoesNotThrow( () -> likeService.deleteLike(like.getId()) );

        verify( likeRepository, times(1) ).deleteById(like.getId());
    }

    @Test
    void deleteLikeTest_notFound() {
        var like = getLikeEntity();
        when( likeRepository.findById(like.getId()) ).thenReturn( java.util.Optional.empty() );
        
        assertThrows(ResourceNotFoundException.class, () -> likeService.deleteLike(like.getId()));
    }

    @Test
    void countLikesByPostIdTest() {
        var post = getPostEntity();
        int expectedCount = 23;
        when( likeRepository.countByPostId(post.getId()) ).thenReturn( expectedCount );
        
        var result = likeService.countLikesByPostId(post.getId());

        verify( likeRepository, times(1) ).countByPostId(post.getId());
        assertEquals(expectedCount, result);
    }

    private Answer<Like> getPassedObjectAnswer() {
        return invocation -> {
            Like likeEntity = invocation.getArgument(0);
            likeEntity.setId(1L);
            return likeEntity;
        };
    }
}
