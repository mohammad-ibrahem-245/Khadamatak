package org.example.posts.controllers;

import org.example.posts.Models.Reviews;
import org.example.posts.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {



    @Autowired
    private ReviewService reviewService;


    @PostMapping("/add")
    public ResponseEntity<Reviews> create(@RequestBody Reviews review,
                                          @RequestHeader("X-User-Name") String username,
                                          @RequestHeader("X-Is-Admin") boolean isAdmin) {
        return ResponseEntity.ok(reviewService.create(review, username, isAdmin));
    }

    @GetMapping("/allreviews")
    public ResponseEntity<List<Reviews>> findAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reviews> findById(@PathVariable Long id) {
        return reviewService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reviews> update(@PathVariable Long id, @RequestBody Reviews review,
                                          @RequestHeader("X-User-Name") String username,
                                          @RequestHeader("X-Is-Admin") boolean isAdmin) {
        return reviewService.update(id, review, username, isAdmin)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("X-User-Name") String username,
                                       @RequestHeader("X-Is-Admin") boolean isAdmin) {
        return reviewService.delete(id, username, isAdmin)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<List<Reviews>> findByPostId(@PathVariable Long id) {
        return reviewService.findAllByPostId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}

