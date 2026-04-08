package org.example.posts.Services;

import org.example.posts.Models.Post;
import org.example.posts.Repositories.PostsRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostsService {

	private final PostsRepositories postsRepositories;

	public PostsService(PostsRepositories postsRepositories) {
		this.postsRepositories = postsRepositories;
	}

	public Post create(Post post) {
		post.setId(null);
		return postsRepositories.save(post);
	}

	public List<Post> findAll() {
		return postsRepositories.findAll();
	}

	public Optional<Post> findById(Long id) {
		return postsRepositories.findById(id);
	}

	public List<Post> findAllByOwner(Long owner) {
		return postsRepositories.findAllByOwner(owner);
	}

	public Optional<Post> update(Long id, Post updatedPost) {
		return postsRepositories.findById(id).map(existingPost -> {
			existingPost.setTitle(updatedPost.getTitle());
			existingPost.setOwner(updatedPost.getOwner());
			existingPost.setContent(updatedPost.getContent());
			existingPost.setImage(updatedPost.getImage());
			existingPost.setPrice(updatedPost.getPrice());
			return postsRepositories.save(existingPost);
		});
	}

	public boolean delete(Long id) {
		if (!postsRepositories.existsById(id)) {
			return false;
		}
		postsRepositories.deleteById(id);
		return true;
	}
}
