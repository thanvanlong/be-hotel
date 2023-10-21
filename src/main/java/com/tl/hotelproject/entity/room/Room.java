package com.tl.hotelproject.entity.room;

import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.booking.Booking;
import com.tl.hotelproject.utils.StringUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roomTbl")
public class Room extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private String name;
    private double price;
    private String description;
    private String slug;

//    @Column(columnDefinition = "text[]")
    @ElementCollection
    private List<String> images;

//    @OneToMany(mappedBy = "room")
//    private List<BookedRoom> bookedRooms;

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;

    @ManyToMany
    @JoinTable(
            name = "room_featureRoom",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "featureRoom_id")
    )
    private Set<FeatureRoom> featureRooms;

    public void setFeatureRooms(List<String> featureRoomIds) {
        this.featureRooms = featureRoomIds.stream()
                .map(id -> {
                    FeatureRoom featureRoom = new FeatureRoom();
                    featureRoom.setId(id);
                    return featureRoom;
                })
                .collect(Collectors.toSet());
    }

    public void setName(String name) {
        this.name = name;
        this.slug = StringUtils.slugify(name);
    }
}
