package com.tl.hotelproject.entity.client;

import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.booking.Booking;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clientTbl")
public class Client extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private String firstName;
    private String lastName;
    private String address;
    private String sex;
    private String email;
    private String tel;
    private String note;

    @OneToMany(mappedBy = "client")
    private List<Booking> bookingList;
}
