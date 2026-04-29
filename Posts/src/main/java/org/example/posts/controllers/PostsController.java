package org.example.posts.controllers;

import org.example.posts.Models.Post;
import org.example.posts.Services.PostsService;
import org.example.posts.Services.PostsService.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostsController {

    @Autowired
    private PostsService postsService;


    @PostMapping("/add")
    public ResponseEntity<Post> create(@RequestBody Post post,
                                       @RequestHeader("X-User-Id") Long userId,
                                       @RequestHeader("X-Role") UserRole role) {
        return ResponseEntity.ok(postsService.create(post, userId, role));
    }

      @GetMapping("/all")
      public ResponseEntity<List<Post>> findAll(@RequestParam(required = false) Long owner,
                                                @RequestHeader("X-User-Id") Long userId,
                                                @RequestHeader("X-Role") UserRole role) {
        if (owner != null) {
          return ResponseEntity.ok(postsService.findAllByOwner(owner, userId, role));
        }
        return ResponseEntity.ok(postsService.findAll(userId, role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> findById(@PathVariable Long id,
                                         @RequestHeader("X-User-Id") Long userId,
                                         @RequestHeader("X-Role") UserRole role) {
        return postsService.findById(id, userId, role)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post post,
                                      @RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader("X-Role") UserRole role) {
        return postsService.update(id, post, userId, role)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("X-User-Id") Long userId,
                                       @RequestHeader("X-Role") UserRole role) {
        return postsService.delete(id, userId, role)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
