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
    private double price;
    private boolean isCheckedIn;

//    @OneToMany(mappedBy = "booking")
//    private List<BookedRoom> bookedRooms;

    @OneToMany(mappedBy = "booking")
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

//    @OneToMany(mappedBy = "booking")
//    private List<Bill> bills;

}
