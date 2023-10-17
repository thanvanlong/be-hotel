package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking, String> {

}
