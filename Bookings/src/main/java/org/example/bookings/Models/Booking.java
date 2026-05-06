package org.example.bookings.Models;


import jakarta.persistence.*;
import lombok.Data;
import org.example.bookings.Enum.BookingStatus;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Data
public class Booking {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long providerID;
    @Column(nullable = false)
    private Long postId;
    @Column(nullable = false)
    private Date bookingDate;
    @Column(nullable = false)
    private LocalTime bookingTime;
    @Column(nullable = false)
     @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;


}
