package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.Metadata;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.services.Services;
import com.tl.hotelproject.entity.services.UsedServices;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.repo.ClientRepo;
import com.tl.hotelproject.service.room.RoomService;
import com.tl.hotelproject.service.services.ServicesService;
import com.tl.hotelproject.service.services.UsedServicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private UsedServicesService usedServicesService;

    @Autowired
    private ServicesService servicesService;

    @Override
    public String save(AddBookingDto body) throws Exception {
        Room room = roomService.findById(body.getIdRoom());

            // set BooedRoom
//            BookedRoom bookedRoom = new BookedRoom();
//            bookedRoom.setRoom(room);
//            bookedRoom.setCheckin(body.getCheckin());
//            bookedRoom.setCheckout(body.getCheckout());
//            bookedRoomService.save(bookedRoom);

            // set new client
            Client client = new Client();
            client.setFirstName(body.getFirstName());
            client.setLastName(body.getLastName());
            client.setTel(body.getTel());
            client.setEmail(body.getEmail());
            client.setSex(body.getSex());
            client.setName();
            clientRepo.save(client);

//            List<BookedRoom> roomList = new ArrayList<>();
//            roomList.add(bookedRoom);

            Booking booking = new Booking();
            booking.setClient(client);
            booking.setRoom(room);
            booking.setCheckin(body.getCheckin());
            booking.setCheckout(body.getCheckout());
            booking.setPrice(room.getPrice());
            bookingRepo.save(booking);

            return "Book room is success!";
    }

    @Override
    public String updateUsedService(UpdateUsedServicesDto body) throws Exception {
        Booking booking = this.getBookingWithRelationship(body.getId());
        Services services = this.servicesService.findById(body.getId());

        // tao usedService
        UsedServices usedServices = new UsedServices();
        usedServices.setServices(services.getId());
        usedServices.setQuantity(body.getQuantity());
        usedServices.setPrice(services.getPrice());

        usedServices = this.usedServicesService.save(usedServices);

        List<UsedServices> listUsed = booking.getUsedServices();
        listUsed.add(usedServices);

        bookingRepo.save(booking);

        return "Da cap nhat thanh cong";
    }

    @Override
    public Booking findById(String id) throws Exception {
        Optional<Booking> booking = this.bookingRepo.findById(id);
        if(booking.isPresent()) return booking.get();
        throw new Exception("Booking khong ton tai");
    }

    @Override
    public Booking getBookingWithRelationship(String id){
       return this.bookingRepo.getBookingWithRelationship(id);
    }

    @Override
    public Map<String, Object> pagingSort(int page, int limit) {
        Pageable pagingSort = PageRequest.of(page, limit);
        Page<Booking> bookingPage = bookingRepo.findAll(pagingSort);

        Metadata metadata = new Metadata();
        metadata.setPageNumber(bookingPage.getNumber());
        metadata.setPageSize(bookingPage.getSize());
        metadata.setTotalPages(bookingPage.getTotalPages());
        metadata.setTotalItems(bookingPage.getTotalElements());

        Map<String, Object> response = new HashMap<>();
        response.put("results", bookingPage.getContent());
        response.put("metadata", metadata);


        return response;
    }
}
