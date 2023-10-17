package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.entity.booking.BookedRoom;
import org.springframework.stereotype.Service;

@Service
public interface BookedRoomService {
    void save(BookedRoom bookedRoom) throws Exception;
}
