package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.entity.booking.BookedRoom;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.repo.ClientRepo;
import com.tl.hotelproject.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookedRoomService bookedRoomService;

    @Autowired
    private ClientRepo clientRepo;

    @Override
    public String save(AddBookingDto body) throws Exception {
        Room room = roomService.findById(body.getIdRoom());

            // set BooedRoom
            BookedRoom bookedRoom = new BookedRoom();
            bookedRoom.setRoom(room);
            bookedRoom.setCheckin(body.getCheckin());
            bookedRoom.setCheckout(body.getCheckout());
            bookedRoomService.save(bookedRoom);

            // set new client
            Client client = new Client();
            client.setFirstName(body.getFirstName());
            client.setLastName(body.getLastName());
            client.setTel(body.getTel());
            client.setEmail(body.getEmail());
            client.setSex(body.getSex());
            clientRepo.save(client);

            List<BookedRoom> roomList = new ArrayList<>();
            roomList.add(bookedRoom);

            Booking booking = new Booking();
            booking.setBookedRooms(roomList);
            booking.setClient(client);
            bookingRepo.save(booking);

            return "Book room is success!";
    }

    @Override
    public String addService() throws Exception {
        return null;
    }
}
