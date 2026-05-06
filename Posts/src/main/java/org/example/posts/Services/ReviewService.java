package org.example.posts.Services;

import org.example.posts.Models.Reviews;
import org.example.posts.Repositories.ReviewRepository;
import org.example.posts.Services.PostsService.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {


    @Autowired
    private ReviewRepository reviewRepository;


    public Reviews create(Reviews review, Long userId, UserRole role) {
        review.setId(null);
        review.setAuthor(isAdmin(role) && review.getAuthor() != null ? review.getAuthor() : userId);
        return reviewRepository.save(review);
    }

    public List<Reviews> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Reviews> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public Optional <List<Reviews>> findAllByPostId(long postId) {
        return Optional.of(reviewRepository.findAllByPostId(postId));
    }

    public Optional<Reviews> update(Long id, Reviews updatedReview, Long userId, UserRole role) {
        return reviewRepository.findById(id).map(existingReview -> {
            assertReviewAccess(existingReview, userId, role);
            existingReview.setContent(updatedReview.getContent());
            existingReview.setPostId(updatedReview.getPostId());
            existingReview.setRating(updatedReview.getRating());
            if (isAdmin(role) && updatedReview.getAuthor() != null) {
                existingReview.setAuthor(updatedReview.getAuthor());
            }
            return reviewRepository.save(existingReview);
        });
    }

    public boolean delete(Long id, Long userId, UserRole role) {
        Reviews review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        assertReviewAccess(review, userId, role);
        reviewRepository.deleteById(id);
        return true;
    }

    private void assertReviewAccess(Reviews review, Long userId, UserRole role) {
        if (!isAdmin(role) && !review.getAuthor().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own reviews");
        }
    }

    private boolean isAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }
}

