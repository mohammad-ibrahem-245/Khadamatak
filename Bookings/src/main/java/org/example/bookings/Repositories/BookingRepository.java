package org.example.bookings.Repositories;

import org.example.bookings.Models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByUserId(Long userId);

    List<Booking> findAllByProviderID(Long providerID);
}

