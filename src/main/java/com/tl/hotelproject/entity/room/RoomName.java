package com.tl.hotelproject.entity.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.utils.StringUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roomName")
public class RoomName extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String name;

    private String search;
    private boolean isBooking;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private Room room;

    public void setName(String name) {
        this.name = name;
        this.search = StringUtils.removeAccents(name).toLowerCase();
    }
}
