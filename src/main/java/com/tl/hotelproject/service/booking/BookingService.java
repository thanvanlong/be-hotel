package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.booking.Booking;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface BookingService {
    String save(AddBookingDto body, int discount) throws Exception;

    String updateUsedService(String id, UpdateUsedServicesDto[] body) throws Exception;

    Booking findById(String id) throws Exception;
    Booking getBookingWithRelationship(String id);

    Map<String, Object> pagingSort(int page, int limit);

    String checkIn(String id) throws Exception;

    Map<String, Object> search(String search, int page, int limit);

}
