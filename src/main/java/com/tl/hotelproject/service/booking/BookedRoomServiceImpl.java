package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.booking.BookedRoom;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.services.Services;
import com.tl.hotelproject.entity.services.UsedServices;
import com.tl.hotelproject.repo.BookedRoomRepo;
import com.tl.hotelproject.service.services.ServicesService;
import com.tl.hotelproject.service.services.UsedServicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookedRoomServiceImpl implements BookedRoomService{
    @Autowired
    private BookedRoomRepo bookedRoomRepo;

    @Autowired
    private ServicesService servicesService;

    @Autowired
    private UsedServicesService usedServicesService;

    @Override
    public void save(BookedRoom bookedRoom) throws Exception {
        bookedRoomRepo.save(bookedRoom);
    }

//    @Override
//    public String update(UpdateUsedServicesDto body) throws Exception {
//        BookedRoom bookedRoom = this.getBookedRoomWithRelationship(body.getId());
//        Services services = this.servicesService.findById(body.getId());
//
//        // tao usedService
//        UsedServices usedServices = new UsedServices();
//        usedServices.setServices(services.getId());
//        usedServices.setQuantity(body.getQuantity());
//        usedServices.setPrice(services.getPrice());
//
//        usedServices = this.usedServicesService.save(usedServices);
//
//        List<UsedServices> listUsed = bookedRoom.getUsedServices();
//        listUsed.add(usedServices);
//
//        bookedRoom.setUsedServices(listUsed);
//        this.save(bookedRoom);
//
//        return "Da cap nhat thanh cong";
//    }

    @Override
    public BookedRoom findById(String id) throws Exception {
        Optional<BookedRoom> bookedRoom = bookedRoomRepo.findById(id);
        if (bookedRoom.isPresent()) return bookedRoom.get();
        throw new Exception("Booked Room khong ton tai");
    }

//    @Override
//    public BookedRoom getBookedRoomWithRelationship(String id) {
//        return this.bookedRoomRepo.getBookedRoomWithRelationship(id);
//    }
}
