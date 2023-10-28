package com.tl.hotelproject.entity.promotion;

import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.State;
import com.tl.hotelproject.utils.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @UuidGenerator
    private String id;

    private String name;
    private String description;
    private String image;
    private String slug;
    private LocalDate startDate;
    private LocalDate endDate;
    private int discount;
    private State state = State.Active;

    @CreationTimestamp
    private LocalDateTime createAt;
    @UpdateTimestamp
    private LocalDateTime updateAt;

    public void setSlug() {
        this.slug = StringUtils.slugify(this.name);
    }
}
