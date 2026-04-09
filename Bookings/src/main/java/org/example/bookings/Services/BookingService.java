package org.example.bookings.Services;

import org.example.bookings.Enum.BookingStatus;
import org.example.bookings.Models.Booking;
import org.example.bookings.Repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking create(Booking booking) {
        booking.setId(null);
        if (booking.getBookingStatus() == null) {
            booking.setBookingStatus(BookingStatus.PENDING);
        }
        return bookingRepository.save(booking);
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> findAllByUserId(Long userId) {
        return bookingRepository.findAllByUserId(userId);
    }

    public List<Booking> findAllByProviderID(Long providerID) {
        return bookingRepository.findAllByProviderID(providerID);
    }

    public Optional<Booking> update(Long id, Booking updatedBooking) {
        return bookingRepository.findById(id).map(existingBooking -> {
            existingBooking.setUserId(updatedBooking.getUserId());
            existingBooking.setProviderID(updatedBooking.getProviderID());
            existingBooking.setBookingDate(updatedBooking.getBookingDate());
            existingBooking.setBookingTime(updatedBooking.getBookingTime());
            existingBooking.setLongitude(updatedBooking.getLongitude());
            existingBooking.setLatitude(updatedBooking.getLatitude());
            return bookingRepository.save(existingBooking);
        });
    }

    public Optional<Booking> updateStatus(Long id, BookingStatus bookingStatus) {
        return bookingRepository.findById(id).map(existingBooking -> {
            existingBooking.setBookingStatus(bookingStatus);
            return bookingRepository.save(existingBooking);
        });
    }

    public boolean delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            return false;
        }
        bookingRepository.deleteById(id);
        return true;
    }
}

