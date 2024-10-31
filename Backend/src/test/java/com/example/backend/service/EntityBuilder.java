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
import com.example.backend.model.User;

public abstract class EntityBuilder {

    private static final Random random = new Random();

    protected FAQ getFaqEntity() {
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        FAQ faq = new FAQ();

        faq.setId(randomValue);
        faq.setQuestion("Question" + randomValue);
        faq.setAnswer("Answer" + randomValue);
        faq.setUser(null);
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
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        Post post = new Post();

        post.setId(randomValue);
        post.setTitle("Title" + randomValue);
        post.setContent("Content" + randomValue);
        post.setUser(null);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        return post;
    }

    protected Like getLikeEntity() {
        Long randomValue = EntityBuilder.random.nextLong(Long.MAX_VALUE);
        Like like = new Like();

        like.setId(randomValue);
        like.setUser(getUserEntity());
        like.setPost(getPostEntity());
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
