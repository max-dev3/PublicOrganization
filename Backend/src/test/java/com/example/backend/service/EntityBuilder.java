package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.example.backend.model.FAQ;
import com.example.backend.model.HasIdentifier;
import com.example.backend.model.Like;
import com.example.backend.model.Post;
import com.example.backend.model.Role;
import com.example.backend.model.Status;
import com.example.backend.model.User;

public abstract class EntityBuilder {

    private static final Random random = new Random();

    protected FAQ getFaqEntity() {
        return getFaqEntity(null);
    }

    protected FAQ getFaqEntity(User user) {
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        return getFaqEntity(user, randomValue);
    }

    protected FAQ getFaqEntity(User user, Long id) {
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        FAQ faq = new FAQ();

        faq.setId(id);
        faq.setQuestion("Question" + randomValue);
        faq.setAnswer("Answer" + randomValue);
        faq.setUser(user);
        faq.setCreatedAt(new Date());
        faq.setUpdatedAt(new Date());
        return faq;
    }

    protected User getUserEntity() {
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        User user = new User();

        user.setId(randomValue);
        user.setFirstName("First" + randomValue);
        user.setLastName("Last" + randomValue);
        String randomDigits = String.format("%010d", random.nextLong(1_000_000_0000L));
        user.setPhoneNumber("+38" + randomDigits);
        user.setEmail("email" + randomValue + "@example.com");
        user.setUsername("Username" + randomValue);
        user.setPassword("Password" + randomValue);
        user.setRole(Role.USER);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return user;
    }

    protected Post getPostEntity() {
        return getPostEntity(null);
    }

    protected Post getPostEntity(User user) {
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        return getPostEntity(user, randomValue);
    }

    protected Post getPostEntity(User user, Long id) {
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        Post post = new Post();

        post.setId(id);
        post.setTitle("Title" + randomValue);
        post.setContent("Content" + randomValue);
        post.setUser(user);
        post.setStatus(Status.PENDING_APPROVAL);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        return post;
    }

    protected Like getLikeEntity() {
        return getLikeEntity( getUserEntity(), getPostEntity() );
    }

    protected Like getLikeEntity(User user, Post post) {
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        Like like = new Like();

        like.setId(randomValue);
        like.setUser(user);
        like.setPost(post);
        return like;
    }

    // TODO: move this to a utility class
    protected <T extends HasIdentifier> void assertListEquals(List<T> expected, List<T> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals( expected.get(i).getId(), actual.get(i).getId() );
        }
    }
}
