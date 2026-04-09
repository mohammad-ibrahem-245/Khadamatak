package org.example.posts.controllers;

import org.example.posts.Models.Favirote;
import org.example.posts.Services.FaviroteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favirote")
public class FaviroteController {

    private final FaviroteService faviroteService;

    public FaviroteController(FaviroteService faviroteService) {
        this.faviroteService = faviroteService;
    }

    @PostMapping
    public ResponseEntity<Void> add(@RequestBody Favirote favirote) {
        boolean added = faviroteService.add(favirote.getUserId(), favirote.getPostId());
        if (!added) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long postId) {
        boolean deleted = faviroteService.delete(userId, postId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

