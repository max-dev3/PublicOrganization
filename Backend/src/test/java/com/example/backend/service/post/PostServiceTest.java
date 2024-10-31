package com.example.backend.service.post;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.backend.exception.InvalidInputException;
import com.example.backend.model.Post;
import com.example.backend.model.Status;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.EntityBuilder;
import com.example.backend.service.PostService;

public class PostServiceTest extends EntityBuilder {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;

    private PostService postService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        postService = new PostService(postRepository, userRepository);
    }

    @Test
    void getAllPostsTest() {
        var postList = List.of( getPostEntity(), getPostEntity() );
        when( postRepository.findAll() ).thenReturn( postList );

        var result = postService.getAllPosts();

        verify( postRepository, times(1) ).findAll();
        assertEquals(2, result.size());
        assertListEquals(postList, result);
    }

    @Test
    void getAllApprovedPostsTest() {
        var postList = List.of( getPostEntity(), getPostEntity() );
        when( postRepository.findByStatus(Status.APPROVED) ).thenReturn( postList );

        var result = postService.getAllApprovedPosts();

        verify( postRepository, times(1) ).findByStatus(Status.APPROVED);
        assertEquals(2, result.size());
        assertListEquals(postList, result);
    }

    @Test
    void getPostByIdTest() {
        var post = getPostEntity();
        when( postRepository.findById(post.getId()) ).thenReturn( java.util.Optional.of(post) );

        var result = postService.getPostById(post.getId());

        verify( postRepository, times(1) ).findById(post.getId());
        assertEquals(post.getId(), result.get().getId());
    }

    @Test
    void getLikedPostsByUserIdTest() {
        var postList = List.of( getPostEntity(), getPostEntity() );
        when( postRepository.findLikedPostsByUserId(1L) ).thenReturn( postList );

        var result = postService.getLikedPostsByUserId(1L);

        verify( postRepository, times(1) ).findLikedPostsByUserId(1L);
        assertEquals(2, result.size());
        assertListEquals(postList, result);
    }

    @Test
    void createPostTest() {
        // setup
        var user = getUserEntity();
        var post = getPostEntity();
        post.setUser(user);
        when( userRepository.findById(user.getId()) ).thenReturn( java.util.Optional.of(user) );
        when( postRepository.save(post) ).thenAnswer( invocation -> invocation.getArgument(0) );

        assertNotEquals( Status.PENDING_APPROVAL, post.getStatus() );

        // test
        var result = postService.createPost(post);

        // assertions
        verify(userRepository, times(1) ).findById(user.getId());
        verify(postRepository, times(1)).save( eq(post) );

        assertEquals(post.getId(), result.getId());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(Status.PENDING_APPROVAL, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void craetePostThrowsInvalidInputExceptionTest() {
        var user = getUserEntity();
        var post = getPostEntity();
        post.setUser(user);
        when( userRepository.findById(post.getUser().getId()) ).thenReturn( java.util.Optional.empty() );

        var exception = assertThrows(InvalidInputException.class, () -> postService.createPost(post));
        assertEquals("User with id " + post.getUser().getId() + " not found.", exception.getMessage());
        verify( postRepository, never() ).save( any(Post.class) );
    }

    @Test
    void deletePostTest() {
        postService.deletePost(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    void getPostsByUserIdTest() {
        var postList = List.of( getPostEntity(), getPostEntity() );
        when( postRepository.findByUserId(1L) ).thenReturn( postList );

        var result = postService.getPostsByUserId(1L);

        verify( postRepository, times(1) ).findByUserId(1L);
        assertEquals(2, result.size());
        assertListEquals(postList, result);
    }

    @Test
    void approvePostTest() {
        var post = getPostEntity();
        when( postRepository.findById(post.getId()) ).thenReturn( java.util.Optional.of(post) );
        when( postRepository.save(post) ).thenAnswer( invocation -> invocation.getArgument(0) );

        var result = postService.approvePost(post.getId());

        verify( postRepository, times(1) ).findById(post.getId());
        verify( postRepository, times(1) ).save(post);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void approvePostThrowsInvalidInputExceptionTest() {
        when( postRepository.findById(1L) ).thenReturn( java.util.Optional.empty() );

        var exception = assertThrows(InvalidInputException.class, () -> postService.approvePost(1L));
        
        assertEquals("Post not found with id: 1", exception.getMessage());
        verify( postRepository, times(1) ).findById(1L);
        verify( postRepository, never() ).save( any(Post.class) );
    }

    @Test
    void rejectPostTest() {
        var post = getPostEntity();
        when( postRepository.findById(post.getId()) ).thenReturn( java.util.Optional.of(post) );
        when( postRepository.save(post) ).thenAnswer( invocation -> invocation.getArgument(0) );

        var result = postService.rejectPost(post.getId());

        verify( postRepository, times(1) ).findById(post.getId());
        verify( postRepository, times(1) ).save(post);
        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void rejectPostThrowsInvalidInputExceptionTest() {
        when( postRepository.findById(1L) ).thenReturn( java.util.Optional.empty() );

        var exception = assertThrows(InvalidInputException.class, () -> postService.rejectPost(1L));
        
        assertEquals("Post not found with id: 1", exception.getMessage());
        verify( postRepository, times(1) ).findById(1L);
        verify( postRepository, never() ).save( any(Post.class) );
    }

    @Test
    void getPostsByStatusTest() {
        var postList = List.of( getPostEntity(), getPostEntity() );
        when( postRepository.findByStatus(Status.PENDING_APPROVAL) ).thenReturn( postList );

        var result = assertDoesNotThrow( () -> postService.getPostsByStatus(Status.PENDING_APPROVAL.name()) );

        verify( postRepository, times(1) ).findByStatus(Status.PENDING_APPROVAL);
        assertEquals(2, result.size());
        assertListEquals(postList, result);
    }

    @Test
    void getPostsByStatusThrowsInvalidInputExceptionTest() {
        
        var exception = assertThrows(
                InvalidInputException.class, 
                () -> postService.getPostsByStatus("some invalid status"));
        
        assertEquals("Invalid status: some invalid status", exception.getMessage());
        verify( postRepository, never() ).findByStatus(any(Status.class));
    }
}
