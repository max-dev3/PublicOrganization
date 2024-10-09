package com.example.backend.repository;

import com.example.backend.model.Post;
import com.example.backend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(Status approved);

    List<Post> findByUserId(Long userId);
    @Query("SELECT l.post FROM Like l WHERE l.user.id = :userId")
    List<Post> findLikedPostsByUserId(@Param("userId") Long userId);

}
