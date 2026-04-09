package org.example.posts.Repositories;

import org.example.posts.Models.Favirote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaviroteRepository extends JpaRepository<Favirote, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    long deleteByUserIdAndPostId(Long userId, Long postId);
}

