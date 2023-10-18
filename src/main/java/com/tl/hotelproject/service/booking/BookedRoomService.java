package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.booking.BookedRoom;
import org.springframework.stereotype.Service;

@Service
public interface BookedRoomService {
    void save(BookedRoom bookedRoom) throws Exception;
//    String update(UpdateUsedServicesDto body) throws Exception;

    BookedRoom findById(String id) throws Exception;

//    BookedRoom getBookedRoomWithRelationship(String id);
}
