package com.tl.hotelproject.entity.bill;

import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "billTbl")
public class Bill extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private Date paymentDate;
    private double totalAmount;
    private String note;
    private String paymentType;

    @ManyToOne()
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

}
