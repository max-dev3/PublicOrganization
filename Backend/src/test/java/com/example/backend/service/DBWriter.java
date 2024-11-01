package com.example.backend.service;

import org.springframework.boot.test.context.SpringBootTest;

import com.example.backend.model.FAQ;
import com.example.backend.model.Like;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.FAQRepository;
import com.example.backend.repository.LikeRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;

@SpringBootTest(
    properties = { "spring.datasource.url=jdbc:tc:mysql:8.0.39:///test" }
)
public abstract class DBWriter extends EntityBuilder {

    protected static final Long NEW_ENTITY_ID = 0L;

    protected User writeUserToDB(User entity) {
        return getUserRepository().save(entity);
    }

    protected User writeUserToDB() {
        return writeUserToDB( getUserEntity() );
    }

    protected FAQ writeFAQToDB(FAQ faq) {
        return getFaqRepository().save(faq);
    }

    protected Post writePostToDB() {
        return writePostToDB( getPostEntity( writeUserToDB() ) );
    }

    protected Post writePostToDB(Post post) {
        return getPostRepository().save(post);
    }

    protected Like writeLikeToDB(Like entity) {
        return getLikeRepository().save(entity);
    }

    protected  PostRepository getPostRepository() {
        return null;
    };

    protected FAQRepository getFaqRepository() {
        return null;
    }

    protected LikeRepository getLikeRepository() {
        return null;
    }

    protected abstract UserRepository getUserRepository();
}
