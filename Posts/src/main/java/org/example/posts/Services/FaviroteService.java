package org.example.posts.Services;

import jakarta.transaction.Transactional;
import org.example.posts.Models.Favirote;
import org.example.posts.Repositories.FaviroteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FaviroteService {

    private final FaviroteRepository faviroteRepository;

    public FaviroteService(FaviroteRepository faviroteRepository) {
        this.faviroteRepository = faviroteRepository;
    }

    public boolean add(Long userId, Long postId, Long currentUserId, boolean isAdmin) {
        if (!isAdmin) {
            userId = currentUserId;
        }
        if (faviroteRepository.existsByUserIdAndPostId(userId, postId)) {
            return false;
        }
        faviroteRepository.save(new Favirote(null, userId, postId));
        return true;
    }

    @Transactional
    public boolean delete(Long userId, Long postId, Long currentUserId, boolean isAdmin) {
        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own favorite");
        }
        if (!faviroteRepository.existsByUserIdAndPostId(userId, postId)) {
            return false;
        }
        faviroteRepository.deleteByUserIdAndPostId(userId, postId);
        return true;
    }
}

