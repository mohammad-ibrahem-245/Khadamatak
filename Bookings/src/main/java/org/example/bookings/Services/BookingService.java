package org.example.bookings.Services;

import org.example.bookings.Enum.BookingStatus;
import org.example.bookings.FeignClients.NotificationClient;
import org.example.bookings.Models.Booking;
import org.example.bookings.Models.NotificationRequest;
import org.example.bookings.Repositories.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    public enum UserRole {
        USER,
        PROVIDER,
        ADMIN
    }

    private final BookingRepository bookingRepository;
    private final NotificationClient notificationClient;

    public BookingService(BookingRepository bookingRepository, NotificationClient notificationClient) {
        this.bookingRepository = bookingRepository;
        this.notificationClient = notificationClient;
    }

    public Booking create(Booking booking, Long currentUserId, UserRole role) {
        booking.setId(null);
        if (booking.getBookingStatus() == null) {
            booking.setBookingStatus(BookingStatus.PENDING);
        }
        if (!isAdmin(role)) {
            booking.setUserId(currentUserId);
        }
        Booking createdBooking = bookingRepository.save(booking);
        notifyUser(createdBooking.getProviderID(), "You have a new booking request from user " + createdBooking.getUserId());
        notifyUser(createdBooking.getUserId(), "Your booking request was created and is pending");
        return createdBooking;
    }

    public List<Booking> findAll(Long currentUserId, UserRole role) {
        if (isAdmin(role)) {
            return bookingRepository.findAll();
        }
        if (isProvider(role)) {
            return bookingRepository.findAllByProviderID(currentUserId);
        }
        return bookingRepository.findAllByUserId(currentUserId);
    }

    public Optional<Booking> findById(Long id, Long currentUserId, UserRole role) {
        return bookingRepository.findById(id).map(booking -> {
            assertBookingAccess(booking, currentUserId, role);
            return booking;
        });
    }

    public List<Booking> findAllByUserId(Long userId, Long currentUserId, UserRole role) {
        if (!isAdmin(role) && !userId.equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own bookings");
        }
        return bookingRepository.findAllByUserId(userId);
    }

    public List<Booking> findAllByProviderID(Long providerID, Long currentUserId, UserRole role) {
        if (!isAdmin(role) && (!isProvider(role) || !providerID.equals(currentUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own provider bookings");
        }
        return bookingRepository.findAllByProviderID(providerID);
    }

    public Optional<Booking> update(Long id, Booking updatedBooking, Long currentUserId, UserRole role) {
        return bookingRepository.findById(id).map(existingBooking -> {
            assertBookingAccess(existingBooking, currentUserId, role);
            existingBooking.setUserId(updatedBooking.getUserId());
            existingBooking.setProviderID(updatedBooking.getProviderID());
            existingBooking.setBookingDate(updatedBooking.getBookingDate());
            existingBooking.setBookingTime(updatedBooking.getBookingTime());
            if (!isAdmin(role)) {
                existingBooking.setUserId(currentUserId);
            }
            return bookingRepository.save(existingBooking);
        });
    }

    public Optional<Booking> updateStatus(Long id, BookingStatus bookingStatus, Long currentUserId, UserRole role) {
        return bookingRepository.findById(id).map(existingBooking -> {
            if (isAdmin(role)) {
                existingBooking.setBookingStatus(bookingStatus);
                Booking savedBooking = bookingRepository.save(existingBooking);
                notifyUser(savedBooking.getUserId(), "Your booking status is now " + bookingStatus);
                return savedBooking;
            }

            if (bookingStatus == BookingStatus.CANCELLED) {
                if (!existingBooking.getUserId().equals(currentUserId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the booking owner can cancel it");
                }
            } else {
                if (!isProvider(role) || !existingBooking.getProviderID().equals(currentUserId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned provider can change this status");
                }
            }

            existingBooking.setBookingStatus(bookingStatus);
            Booking savedBooking = bookingRepository.save(existingBooking);
            notifyUser(savedBooking.getUserId(), "Your booking status is now " + bookingStatus);
            if (bookingStatus == BookingStatus.CANCELLED) {
                notifyUser(savedBooking.getProviderID(), "Booking " + savedBooking.getId() + " was cancelled by the user");
            }
            return savedBooking;
        });
    }

    public boolean delete(Long id, Long currentUserId, UserRole role) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!isAdmin(role) && !booking.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own bookings");
        }
        bookingRepository.deleteById(id);
        notifyUser(booking.getProviderID(), "Booking " + booking.getId() + " was deleted");
        return true;
    }

    private void notifyUser(Long userId, String message) {
        try {
            notificationClient.addNotification(new NotificationRequest(userId, message));
        } catch (Exception ignored) {
            // Best effort: booking workflow should not fail if notifications service is down.
        }
    }

    private void assertBookingAccess(Booking booking, Long currentUserId, UserRole role) {
        if (isAdmin(role)) {
            return;
        }
        boolean owner = booking.getUserId().equals(currentUserId);
        boolean assignedProvider = isProvider(role) && booking.getProviderID().equals(currentUserId);
        if (!owner && !assignedProvider) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own bookings");
        }
    }

    private boolean isAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }

    private boolean isProvider(UserRole role) {
        return role == UserRole.PROVIDER;
    }
}

