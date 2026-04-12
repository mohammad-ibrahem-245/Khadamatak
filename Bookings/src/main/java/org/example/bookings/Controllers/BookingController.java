package org.example.bookings.Controllers;

import org.example.bookings.Enum.BookingStatus;
import org.example.bookings.Models.Booking;
import org.example.bookings.Services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
			@RequestHeader("X-Is-Admin") boolean isAdmin) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(booking, currentUserId, isAdmin));
	}

	@GetMapping
	public ResponseEntity<List<Booking>> findAll(
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) Long providerId,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Is-Admin") boolean isAdmin,
			@RequestHeader("X-Is-Provider") boolean isProvider) {

		if (userId != null) {
			return ResponseEntity.ok(bookingService.findAllByUserId(userId, currentUserId, isAdmin));
		}
		if (providerId != null) {
			return ResponseEntity.ok(bookingService.findAllByProviderID(providerId, currentUserId, isAdmin, isProvider));
		}
		return ResponseEntity.ok(bookingService.findAll(currentUserId, isAdmin, isProvider));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Booking> findById(@PathVariable Long id,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Is-Admin") boolean isAdmin,
			@RequestHeader("X-Is-Provider") boolean isProvider) {
		return bookingService.findById(id, currentUserId, isAdmin, isProvider)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Booking> update(@PathVariable Long id, @RequestBody Booking booking,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Is-Admin") boolean isAdmin,
			@RequestHeader("X-Is-Provider") boolean isProvider) {
		return bookingService.update(id, booking, currentUserId, isAdmin, isProvider)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Booking> updateStatus(
			@PathVariable Long id,
			@RequestParam BookingStatus status,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Is-Admin") boolean isAdmin,
			@RequestHeader("X-Is-Provider") boolean isProvider) {

		return bookingService.updateStatus(id, status, currentUserId, isAdmin, isProvider)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Long id,
			@RequestHeader("X-User-Id") Long currentUserId,
			@RequestHeader("X-Is-Admin") boolean isAdmin) {
		return bookingService.delete(id, currentUserId, isAdmin)
				? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}

}
