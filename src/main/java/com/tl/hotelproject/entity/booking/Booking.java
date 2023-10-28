package com.tl.hotelproject.entity.booking;

import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.client.Client;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.services.UsedServices;
import com.tl.hotelproject.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private Date bookingDate;
    private double selloff;
    private String note;
    private Date checkin;
    private Date checkout;
    private int price;

    @Column(nullable = true)
    private int quantity;
    private boolean isCheckedIn;

    @Column(nullable = true)
    private int totalAmount;
    private BookingState bookingState = BookingState.Init;

//    @OneToMany(mappedBy = "booking")
//    private List<BookedRoom> bookedRooms;

    @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER)
    private List<UsedServices> usedServices;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "booking")
    private List<Bill> bills;

    public void setTotalAmount() {
        // Tính số ngày giữa checkin và checkout
        long diffInMillies = Math.abs(checkout.getTime() - checkin.getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        int totalAmount = 0;
        if(usedServices != null) totalAmount = usedServices.stream()
                .map(x -> x.getPrice() * x.getQuantity())
                .reduce(0, Integer::sum);

        this.totalAmount = totalAmount + this.quantity * this.price * (int)diffInDays;
    }
}
