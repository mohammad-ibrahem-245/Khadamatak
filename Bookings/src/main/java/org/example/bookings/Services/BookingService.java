package org.example.bookings.Services;

import org.example.bookings.Enum.BookingStatus;
import org.example.bookings.Models.Booking;
import org.example.bookings.Repositories.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking create(Booking booking, Long currentUserId, boolean isAdmin) {
        booking.setId(null);
        if (booking.getBookingStatus() == null) {
            booking.setBookingStatus(BookingStatus.PENDING);
        }
        if (!isAdmin) {
            booking.setUserId(currentUserId);
        }
        return bookingRepository.save(booking);
    }

    public List<Booking> findAll(Long currentUserId, boolean isAdmin, boolean isProvider) {
        if (isAdmin) {
            return bookingRepository.findAll();
        }
        if (isProvider) {
            return bookingRepository.findAllByProviderID(currentUserId);
        }
        return bookingRepository.findAllByUserId(currentUserId);
    }

    public Optional<Booking> findById(Long id, Long currentUserId, boolean isAdmin, boolean isProvider) {
        return bookingRepository.findById(id).map(booking -> {
            assertBookingAccess(booking, currentUserId, isAdmin, isProvider);
            return booking;
        });
    }

    public List<Booking> findAllByUserId(Long userId, Long currentUserId, boolean isAdmin) {
        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own bookings");
        }
        return bookingRepository.findAllByUserId(userId);
    }

    public List<Booking> findAllByProviderID(Long providerID, Long currentUserId, boolean isAdmin, boolean isProvider) {
        if (!isAdmin && (!isProvider || !providerID.equals(currentUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own provider bookings");
        }
        return bookingRepository.findAllByProviderID(providerID);
    }

    public Optional<Booking> update(Long id, Booking updatedBooking, Long currentUserId, boolean isAdmin, boolean isProvider) {
        return bookingRepository.findById(id).map(existingBooking -> {
            assertBookingAccess(existingBooking, currentUserId, isAdmin, isProvider);
            existingBooking.setUserId(updatedBooking.getUserId());
            existingBooking.setProviderID(updatedBooking.getProviderID());
            existingBooking.setBookingDate(updatedBooking.getBookingDate());
            existingBooking.setBookingTime(updatedBooking.getBookingTime());
            existingBooking.setLongitude(updatedBooking.getLongitude());
            existingBooking.setLatitude(updatedBooking.getLatitude());
            if (!isAdmin) {
                existingBooking.setUserId(currentUserId);
            }
            return bookingRepository.save(existingBooking);
        });
    }

    public Optional<Booking> updateStatus(Long id, BookingStatus bookingStatus, Long currentUserId, boolean isAdmin, boolean isProvider) {
        return bookingRepository.findById(id).map(existingBooking -> {
            if (isAdmin) {
                existingBooking.setBookingStatus(bookingStatus);
                return bookingRepository.save(existingBooking);
            }

            if (bookingStatus == BookingStatus.CANCELLED) {
                if (!existingBooking.getUserId().equals(currentUserId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the booking owner can cancel it");
                }
            } else {
                if (!isProvider || !existingBooking.getProviderID().equals(currentUserId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned provider can change this status");
                }
            }

            existingBooking.setBookingStatus(bookingStatus);
            return bookingRepository.save(existingBooking);
        });
    }

    public boolean delete(Long id, Long currentUserId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!isAdmin && !booking.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own bookings");
        }
        bookingRepository.deleteById(id);
        return true;
    }

    private void assertBookingAccess(Booking booking, Long currentUserId, boolean isAdmin, boolean isProvider) {
        if (isAdmin) {
            return;
        }
        boolean owner = booking.getUserId().equals(currentUserId);
        boolean assignedProvider = isProvider && booking.getProviderID().equals(currentUserId);
        if (!owner && !assignedProvider) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own bookings");
        }
    }
}

