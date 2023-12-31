package com.tl.hotelproject.entity.services;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.booking.Booking;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usedServices")
public class UsedServices extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;
    private String name;

    private int quantity;
    private int price;

//    @ManyToOne
//    @JoinColumn(name = "usedServices_id")
//    private BookedRoom bookedRoom;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @JsonIgnore
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "services_id")
    private Services services;

//    public void setServices(String servicesId) {
//        Services newServices = new Services();
//        newServices.setId(servicesId);
//
//        this.services = newServices;
//    }

    public void setName(){
        this.name = this.getServices().getName();
    }
}
