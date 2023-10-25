package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepo extends JpaRepository<Booking, String> {
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.room WHERE b.id = :bookingId")
    Booking getBookingWithRelationship(@Param("bookingId") String bookingId);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.client LEFT JOIN FETCH b.bills b1 WHERE b1.id = :billId")
    Booking getBookingByBill(@Param("billId") String billId);
}
