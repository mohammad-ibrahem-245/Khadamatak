package org.example.posts.Services;

import jakarta.transaction.Transactional;
import org.example.posts.Models.Favirote;
import org.example.posts.Repositories.FaviroteRepository;
import org.springframework.stereotype.Service;

@Service
public class FaviroteService {

    private final FaviroteRepository faviroteRepository;

    public FaviroteService(FaviroteRepository faviroteRepository) {
        this.faviroteRepository = faviroteRepository;
    }

    public boolean add(Long userId, Long postId) {
        if (faviroteRepository.existsByUserIdAndPostId(userId, postId)) {
            return false;
        }
        faviroteRepository.save(new Favirote(null, userId, postId));
        return true;
    }

    @Transactional
    public boolean delete(Long userId, Long postId) {
        if (!faviroteRepository.existsByUserIdAndPostId(userId, postId)) {
            return false;
        }
        faviroteRepository.deleteByUserIdAndPostId(userId, postId);
        return true;
    }
}

