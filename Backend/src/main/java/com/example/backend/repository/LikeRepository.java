package com.example.backend.repository;

import com.example.backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    int countByPostId(Long postId);
    @Query("SELECT l FROM Like l WHERE l.post.id = :postId AND l.user.id = :userId")
    Optional<Like> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

}
