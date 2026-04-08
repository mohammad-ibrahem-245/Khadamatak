package org.example.posts.Services;

import org.example.posts.Models.Reviews;
import org.example.posts.Repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {


    @Autowired
    private ReviewRepository reviewRepository;


    public Reviews create(Reviews review) {
        review.setId(null);
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

    public Optional<Reviews> update(Long id, Reviews updatedReview) {
        return reviewRepository.findById(id).map(existingReview -> {
            existingReview.setContent(updatedReview.getContent());
            existingReview.setAuthor(updatedReview.getAuthor());
            existingReview.setPostId(updatedReview.getPostId());
            existingReview.setRating(updatedReview.getRating());
            return reviewRepository.save(existingReview);
        });
    }

    public boolean delete(Long id) {
        if (!reviewRepository.existsById(id)) {
            return false;
        }
        reviewRepository.deleteById(id);
        return true;
    }
}

