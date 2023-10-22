package com.tl.hotelproject.entity.room;

import com.tl.hotelproject.entity.CommonObjectDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "featureRoom")
public class FeatureRoom extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private String name;

    @ManyToMany
    @Transient
    private Set<Room> zooms;
}
