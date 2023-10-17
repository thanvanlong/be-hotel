package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    String save(AddBookingDto body) throws Exception;

    String addService() throws Exception;

}
