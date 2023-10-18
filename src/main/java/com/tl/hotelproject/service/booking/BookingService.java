package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.booking.Booking;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface BookingService {
    String save(AddBookingDto body) throws Exception;

//    String updateUsedService(UpdateUsedServicesDto body) throws Exception;

    Booking findById(String id) throws Exception;
    Booking getBookingWithRelationship(String id);

    Map<String, Object> pagingSort(int page, int limit);

}
