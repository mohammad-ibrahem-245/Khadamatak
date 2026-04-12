package org.example.posts.Services;

import org.example.posts.Models.Reviews;
import org.example.posts.Repositories.ReviewRepository;
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


    public Reviews create(Reviews review, String username, boolean isAdmin) {
        review.setId(null);
        review.setAuthor(isAdmin && review.getAuthor() != null ? review.getAuthor() : username);
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

    public Optional<Reviews> update(Long id, Reviews updatedReview, String username, boolean isAdmin) {
        return reviewRepository.findById(id).map(existingReview -> {
            assertReviewAccess(existingReview, username, isAdmin);
            existingReview.setContent(updatedReview.getContent());
            existingReview.setPostId(updatedReview.getPostId());
            existingReview.setRating(updatedReview.getRating());
            if (isAdmin && updatedReview.getAuthor() != null) {
                existingReview.setAuthor(updatedReview.getAuthor());
            }
            return reviewRepository.save(existingReview);
        });
    }

    public boolean delete(Long id, String username, boolean isAdmin) {
        Reviews review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        assertReviewAccess(review, username, isAdmin);
        reviewRepository.deleteById(id);
        return true;
    }

    private void assertReviewAccess(Reviews review, String username, boolean isAdmin) {
        if (!isAdmin && !review.getAuthor().equalsIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own reviews");
        }
    }
}

