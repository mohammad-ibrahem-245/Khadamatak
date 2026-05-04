package org.example.posts.Services;

import org.example.posts.Models.Post;
import org.example.posts.Repositories.PostsRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Locale;

@Service
public class PostsService {
	public enum UserRole {
		USER,
		PROVIDER,
		ADMIN
	}

	private final PostsRepositories postsRepositories;
	private final NotificationClient notificationClient;

	public PostsService(PostsRepositories postsRepositories, NotificationClient notificationClient) {
		this.postsRepositories = postsRepositories;
		this.notificationClient = notificationClient;
	}

	public Post create(Post post, Long userId, UserRole role) {
		post.setId(null);
		if (!isAdmin(role)) {
			post.setOwner(userId);
		} else if (post.getOwner() == null) {
			post.setOwner(userId);
		}
		Post createdPost = postsRepositories.save(post);
		notifyUser(createdPost.getOwner(), "Your post was created successfully");
		return createdPost;
	}

	public List<Post> findAll(Long userId, UserRole role) {
		return isAdmin(role) ? postsRepositories.findAll() : postsRepositories.findAllByOwner(userId);
	}

	public List<Post> search(String query, Long userId, UserRole role) {
		List<Post> accessiblePosts = isAdmin(role)
				? postsRepositories.findAll()
				: postsRepositories.findAllByOwner(userId);

		if (query == null || query.isBlank()) {
			return accessiblePosts;
		}

		String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
		Long ownerQuery = parseOwnerQuery(query);
		List<Post> filteredPosts = new ArrayList<>();
		for (Post post : accessiblePosts) {
			if (matchesSearch(post, normalizedQuery, ownerQuery)) {
				filteredPosts.add(post);
			}
		}
		return filteredPosts;
	}

	public Optional<Post> findById(Long id, Long userId, UserRole role) {
		return postsRepositories.findById(id).map(post -> {
			assertOwnerOrAdmin(post, userId, role);
			return post;
		});
	}

	public List<Post> findAllByOwner(Long owner, Long userId, UserRole role) {
		if (!isAdmin(role) && !owner.equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own posts");
		}
		return postsRepositories.findAllByOwner(owner);
	}

	public Optional<Post> update(Long id, Post updatedPost, Long userId, UserRole role) {
		return postsRepositories.findById(id).map(existingPost -> {
			assertOwnerOrAdmin(existingPost, userId, role);
			existingPost.setTitle(updatedPost.getTitle());
			existingPost.setContent(updatedPost.getContent());
			existingPost.setImage(updatedPost.getImage());
			existingPost.setPrice(updatedPost.getPrice());
			if (isAdmin(role) && updatedPost.getOwner() != null) {
				existingPost.setOwner(updatedPost.getOwner());
			}
			Post savedPost = postsRepositories.save(existingPost);
			notifyUser(savedPost.getOwner(), "Your post was updated");
			return savedPost;
		});
	}

	public boolean delete(Long id, Long userId, UserRole role) {
		Post post = postsRepositories.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
		assertOwnerOrAdmin(post, userId, role);
		postsRepositories.deleteById(id);
		notifyUser(post.getOwner(), "Your post was deleted");
		return true;
	}

	private void notifyUser(Long userId, String message) {
		try {
			notificationClient.addNotification(new NotificationRequest(userId, message));
		} catch (Exception ignored) {
			// Best effort: post workflow should not fail if notifications service is down.
		}
	}

	private void assertOwnerOrAdmin(Post post, Long userId, UserRole role) {
		if (!isAdmin(role) && !post.getOwner().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own posts");
		}
	}

	private boolean isAdmin(UserRole role) {
		return role == UserRole.ADMIN;
	}

	private boolean matchesSearch(Post post, String normalizedQuery, Long ownerQuery) {
		boolean matchesTitle = post.getTitle() != null && post.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery);
		boolean matchesContent = post.getContent() != null && post.getContent().toLowerCase(Locale.ROOT).contains(normalizedQuery);
		boolean matchesOwner = ownerQuery != null && ownerQuery.equals(post.getOwner());
		return matchesTitle || matchesContent || matchesOwner;
	}

	private Long parseOwnerQuery(String query) {
		try {
			return Long.valueOf(query.trim());
		} catch (NumberFormatException ex) {
			return null;
		}
	}
}
