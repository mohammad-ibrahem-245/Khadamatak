package org.example.bookings.Controllers;

import org.example.bookings.Enum.BookingStatus;
import org.example.bookings.Models.Booking;
import org.example.bookings.Services.BookingService;
import org.example.bookings.Enum.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping
	public ResponseEntity<Booking> create(@RequestBody Booking booking,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Role") UserRole role) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(booking, currentUserId, role));
	}

	@GetMapping
	public ResponseEntity<List<Booking>> findAll(
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) Long providerId,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Role") UserRole role) {

		if (userId != null) {
			return ResponseEntity.ok(bookingService.findAllByUserId(userId, currentUserId, role));
		}
		if (providerId != null) {
			return ResponseEntity.ok(bookingService.findAllByProviderID(providerId, currentUserId, role));
		}
		if (role == UserRole.ADMIN) {
			return ResponseEntity.ok(bookingService.findAll( role));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Booking> findById(@PathVariable Long id,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Role") UserRole role) {
		return bookingService.findById(id, currentUserId, role)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Booking> update(@PathVariable Long id, @RequestBody Booking booking,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Role") UserRole role) {
		return bookingService.update(id, booking, currentUserId, role)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Booking> updateStatus(
			@PathVariable Long id,
			@RequestParam BookingStatus status,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Role") UserRole role) {

		return bookingService.updateStatus(id, status, currentUserId, role)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Role") UserRole role) {
		return bookingService.delete(id, currentUserId, role)
				? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}

}
