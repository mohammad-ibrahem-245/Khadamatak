package org.example.posts.Repositories;

import org.example.posts.Models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepositories extends JpaRepository<Post, Long> {

	List<Post> findAllByOwner(Long owner);
}
