package com.tl.hotelproject.service.booking;

import com.tl.hotelproject.dtos.booking.AddBookingDto;
import com.tl.hotelproject.dtos.booking.UpdateUsedServicesDto;
import com.tl.hotelproject.entity.Metadata;
import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.bill.PaymentFor;
import com.tl.hotelproject.entity.bill.PaymentState;
import com.tl.hotelproject.entity.bill.PaymentType;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.booking.BookingState;
import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.room.RoomName;
import com.tl.hotelproject.entity.services.Services;
import com.tl.hotelproject.entity.services.UsedServices;
import com.tl.hotelproject.repo.BookingRepo;
import com.tl.hotelproject.repo.ClientRepo;
import com.tl.hotelproject.repo.RoomNameRepo;
import com.tl.hotelproject.service.bill.BillService;
import com.tl.hotelproject.service.room.RoomService;
import com.tl.hotelproject.service.services.ServicesService;
import com.tl.hotelproject.service.services.UsedServicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private BillService billService;

    @Autowired
    private RoomNameRepo roomNameRepo;


    @Override
    public String save(AddBookingDto body, int discount, boolean clientBk) throws Exception {
        Room room = roomService.findById(body.getIdRoom());


            // set new client
        Client client = new Client();
        client.setFirstName(body.getFirstName());
        client.setLastName(body.getLastName());
        client.setTel(body.getTel());
        client.setEmail(body.getEmail());
        client.setSex(body.getSex());
        client.setName();
        clientRepo.save(client);

        List<RoomName> roomName = new ArrayList<>();

        for(RoomName roomName1: room.getRoomNames()){
            if(roomName1.isBooking()) continue;
            roomName1.setBooking(true);
            roomName.add(roomName1);

            if(roomName.size() == body.getQuantity()) break;
        }
        if(roomName.size() < body.getQuantity()) throw new Exception("khong du phong");

        // update trang thai phong
        roomNameRepo.saveAll(roomName);
        room.setQuantity(room.getQuantity()-body.getQuantity());
        roomService.update(room);

        Booking booking = new Booking();
        booking.setClient(client);
        booking.setRoom(room);
        booking.setSelloff(discount);
        booking.setCheckin(body.getCheckin());
        booking.setCheckout(body.getCheckout());
        booking.setBookingState(BookingState.AdminInit);
        booking.setPrice((int) (room.getPrice() * ((float) (100 - discount)/100)));
        if(room.getQuantity() < body.getQuantity()) throw new Exception("Khong du so luong phong");
        booking.setQuantity(body.getQuantity());
        booking.setRoomName(roomName.stream().map(RoomName::getName).collect(Collectors.toList()));

        booking.setTotalAmount();
        booking = bookingRepo.save(booking);

        Bill bill = new Bill();
        bill.setOrderId(UUID.randomUUID().toString());
        bill.setTotalAmount(booking.getPrice() * booking.getQuantity());
        bill.setBooking(booking);
        bill.setPaymentFor(PaymentFor.Hotel);

        List<Bill> bills = new ArrayList<>();
        bills.add(bill);
        booking.setBills(bills);

        if(!clientBk) body.setPaymentType(PaymentType.Cash);
        String url = this.billService.initBill(booking, body.getPaymentType());

        if(clientBk)
            return url;
        return "dat phong thanh cong";
    }

    @Override
    public String updateUsedService(String id, UpdateUsedServicesDto[] body) throws Exception {
        Booking booking = this.findById(id);

        List<UsedServices> listT = booking.getUsedServices();
        for (UpdateUsedServicesDto data: body) {
            Services services = this.servicesService.findById(data.getIdService());
            UsedServices usedServices = new UsedServices();
            usedServices.setServices(services);
            usedServices.setQuantity(data.getQuantity());
            usedServices.setPrice(services.getPrice());
            usedServices.setBooking(booking);
            usedServices.setName();
            usedServices = this.usedServicesService.save(usedServices);

            listT.add(usedServices);

        }
        booking.setUsedServices(listT);
        booking.setTotalAmount();

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
//       Booking this.bookingRepo.findById(id);
        return null;
    }

    @Override
    public Map<String, Object> pagingSort(int page, int limit) {
        Pageable pagingSort = PageRequest.of(page, limit, Sort.Direction.ASC, "id");
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

    @Override
    public String checkIn(String id) throws Exception{
        Booking booking = this.findById(id);
        booking.setCheckedIn(true);
        bookingRepo.save(booking);
        return "checkin success";
    }

    @Override
    public String cancel(String id) throws Exception {
        Booking booking = this.findById(id);
        if(booking.isCheckedIn()) throw new Exception("Khong the huy phong");
        booking.setBookingState(BookingState.Reject);
        roomService.revertRoom(booking.getRoom().getId(), booking.getRoomName());
        bookingRepo.save(booking);
        return null;
    }

    @Override
    public String checkOut(String id) throws Exception {
        Booking booking = this.findById(id);
        booking.setBookingState(BookingState.Done);

        if(!booking.isCheckedIn()) throw new Exception("Phong chua checkIn");

        if(booking.getBills().get(0).getPaymentState() == PaymentState.Pending) {
            this.billService.setBillDone(booking.getBills().get(0).getId());
        }

        if(booking.getUsedServices() != null){
            Bill bill = new Bill();
            bill.setTotalAmount(booking.getUsedServices().stream()
                    .map(x -> x.getPrice() * x.getQuantity())
                    .reduce(0, Integer::sum));
            bill.setBooking(booking);
//            bill.setUser(user);
            this.billService.setBillServices(bill);
        }

        // cap nhat lai so phong
        roomService.revertRoom(booking.getRoom().getId(), booking.getRoomName());

        bookingRepo.save(booking);
        return "checkout success";
    }

    @Override
    public Map<String, Object> search(String search, int page, int limit) {
        Pageable pagingSort = PageRequest.of(page, limit, Sort.Direction.DESC, "createdAt");
        Page<Booking>bookingPage = bookingRepo.search(search, pagingSort);

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
