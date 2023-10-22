package com.tl.hotelproject.entity.booking;

import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.room.Room;
import com.tl.hotelproject.entity.services.UsedServices;
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
@Table(name = "bookedRoom")
public class BookedRoom extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private Date checkin;
    private Date checkout;
    private double price;
    private boolean isCheckedIn;
    private double selloff;
    private String note;

//    @ManyToOne
//    @JoinColumn(name = "room_id")
//    private Room room;

//    @ManyToOne
//    @JoinColumn(name = "booking_id")
//    private Booking booking;

//    @OneToMany(mappedBy = "bookedRoom")
//    private List<UsedServices> usedServices;



}
