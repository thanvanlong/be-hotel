package com.tl.hotelproject.entity.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "bill")
public class Bill extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private Date paymentDate = new Date();
    private double totalAmount;
    private String note;
    private String orderId;

    @Enumerated(EnumType.ORDINAL)
    private PaymentType paymentType;

    @Enumerated(EnumType.ORDINAL)
    private PaymentFor paymentFor;

    @Enumerated(EnumType.ORDINAL)
    private PaymentState paymentState;

    @ManyToOne()
    @JoinColumn(name = "booking_id")
    @JsonIgnore
    private Booking booking;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

}
