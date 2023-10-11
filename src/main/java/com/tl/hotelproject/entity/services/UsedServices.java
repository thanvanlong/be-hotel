package com.tl.hotelproject.entity.services;


import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.booking.BookedRoom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usedServicesTbl")
public class UsedServices extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private int quantity;
    private double price;

    @ManyToOne
    @JoinColumn(name = "usedServices_id")
    private BookedRoom bookedRoom;

    @ManyToOne
    @JoinColumn(name = "services_id")
    private Services services;
}
