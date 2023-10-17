package com.tl.hotelproject.repo;

import com.tl.hotelproject.entity.booking.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookedRoomRepo extends JpaRepository<BookedRoom, String> {
}
