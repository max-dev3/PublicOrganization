package com.example.backend.service.post;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.backend.exception.InvalidInputException;
import com.example.backend.model.Status;
import com.example.backend.repository.LikeRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.DBWriter;
import com.example.backend.service.PostService;


public class PostServiceIntegrationTest extends DBWriter {

    @Autowired private LikeRepository likeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    
    @Autowired private PostService postService;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllPostsTest() {
        var user = writeUserToDB();
        var post1 = getPostEntity(user);
        var post2 = getPostEntity(user);
        var posts = List.of( writePostToDB(post1), writePostToDB(post2) );

        var result = assertDoesNotThrow( () -> postService.getAllPosts() );

        assertListEquals(posts, result);
    }

    @Test
    void getAllApprovedPostsTest() {
        var user = writeUserToDB();
        var post1 = getPostEntity(user);
        var post2 = getPostEntity(user);
        post1.setStatus(Status.APPROVED);
        post2.setStatus(Status.APPROVED);
        var posts = List.of( writePostToDB(post1), writePostToDB(post2) );

        var result = assertDoesNotThrow( () -> postService.getAllApprovedPosts() );

        assertListEquals(posts, result);
        assertTrue( result.stream().allMatch( post -> post.getStatus().equals(Status.APPROVED) ) );
    }

    @Test
    void getPostByIdTest() {
        var user = writeUserToDB();
        var post = getPostEntity(user);
        var postId = writePostToDB(post).getId();

        var result = assertDoesNotThrow( () -> postService.getPostById(postId) );

        assertEquals(postId, result.get().getId());
    }

    @Test
    void getLikedPostsByUserId() {
        var user1 = writeUserToDB();
        var user2 = writeUserToDB();
        var post1 = writePostToDB( getPostEntity(user1) );
        var post2 = writePostToDB( getPostEntity(user1) );
        var like1 = getLikeEntity(user1, post1);
        var like2 = getLikeEntity(user2, post2);
        writePostToDB(post1);
        writeLikeToDB(like1);
        writeLikeToDB(like2);
        var posts = List.of( post1 );

        var result = assertDoesNotThrow( () -> postService.getLikedPostsByUserId(user1.getId()) );

        assertListEquals(posts, result);
    }

    @Test
    void createPostTest() {
        var user = writeUserToDB();
        var post = getPostEntity(user);

        var result = assertDoesNotThrow( () -> postService.createPost(post) );

        var postFromDB = postRepository.findById(result.getId());
        assertTrue( postFromDB.isPresent() );
        assertEquals(postFromDB.get().getId(), result.getId());
    }

    @Test
    void createPostUserNotFoundTest() {
        var post = getPostEntity( getUserEntity() );

        var exception = assertThrows(InvalidInputException.class, () -> postService.createPost(post) );
        assertEquals("User with id " + post.getUser().getId() + " not found.", exception.getMessage());
    }

    @Test
    void deletePostTest() {
        var user = writeUserToDB();
        var post = getPostEntity(user);
        var postId = writePostToDB(post).getId();

        assertDoesNotThrow( () -> postService.deletePost(postId) );

        var postFromDB = postRepository.findById(postId);
        assertTrue( postFromDB.isEmpty() );
    }

    @Test
    void getPostsByUserId() {
        var user1 = writeUserToDB();
        var user2 = writeUserToDB();
        var post1 = getPostEntity(user1, null);
        var post2 = getPostEntity(user2, null);
        writePostToDB(post1);
        writePostToDB(post2);
        var posts = List.of( post1);

        var result = assertDoesNotThrow( () -> postService.getPostsByUserId(user1.getId()) );

        assertListEquals(posts, result);
    }

    @Test
    void approvePostTest() {
        var user = writeUserToDB();
        var post = getPostEntity(user);
        var postId = writePostToDB(post).getId();

        var result = assertDoesNotThrow( () -> postService.approvePost(postId) );

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void approvePostNotFoundTest() {
        var postId = 1L;

        var exception = assertThrows(InvalidInputException.class, () -> postService.approvePost(postId) );
        assertEquals("Post not found with id: " + postId, exception.getMessage());
    }

    @Test
    void rejectPostTest() {
        var user = writeUserToDB();
        var post = getPostEntity(user);
        var postId = writePostToDB(post).getId();

        var result = assertDoesNotThrow( () -> postService.rejectPost(postId) );

        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void rejectPostNotFoundTest() {
        var postId = 1L;

        var exception = assertThrows(InvalidInputException.class, () -> postService.rejectPost(postId) );
        assertEquals("Post not found with id: " + postId, exception.getMessage());
    }

    @Override
    protected UserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    protected PostRepository getPostRepository() {
        return postRepository;
    }

    @Override
    protected LikeRepository getLikeRepository() {
        return likeRepository;
    }
}
