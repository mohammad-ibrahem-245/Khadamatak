package org.example.bookings.Controllers;

import org.example.bookings.Enum.BookingStatus;
import org.example.bookings.Models.Booking;
import org.example.bookings.Services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public ResponseEntity<Booking> create(@RequestBody Booking booking) {
		return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(booking));
	}

	@GetMapping
	public ResponseEntity<List<Booking>> findAll(
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) Long providerId) {

		if (userId != null) {
			return ResponseEntity.ok(bookingService.findAllByUserId(userId));
		}
		if (providerId != null) {
			return ResponseEntity.ok(bookingService.findAllByProviderID(providerId));
		}
		return ResponseEntity.ok(bookingService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Booking> findById(@PathVariable Long id) {
		return bookingService.findById(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Booking> update(@PathVariable Long id, @RequestBody Booking booking) {
		return bookingService.update(id, booking)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Booking> updateStatus(
			@PathVariable Long id,
			@RequestParam BookingStatus status,
			@RequestHeader(value = "X-Is-Provider", defaultValue = "false") boolean isProvider) {

		if (!isProvider) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return bookingService.updateStatus(id, status)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		return bookingService.delete(id)
				? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}
}
