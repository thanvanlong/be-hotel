package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.entity.booking.BookedRoom;
import com.tl.hotelproject.repo.BookedRoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookedRoomServiceImpl implements BookedRoomService{
    @Autowired
    private BookedRoomRepo bookedRoomRepo;

    @Override
    public void save(BookedRoom bookedRoom) throws Exception {
        bookedRoomRepo.save(bookedRoom);
    }
}
