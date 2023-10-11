package com.tl.hotelproject.entity.room;

import com.tl.hotelproject.entity.CommonObjectDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "featureRoomTbl")
public class FeatureRoom extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private String name;

    @ManyToMany
    private Set<Room> zooms;
}
