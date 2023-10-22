package com.tl.hotelproject.entity.promotion;

import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.utils.StringUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "promotion")
public class Promotion extends CommonObjectDTO {
    @Id
    @UuidGenerator
    private String id;

    private String name;
    private String description;
    private String image;
    private String slug;

    public void setSlug() {
        this.slug = StringUtils.slugify(this.name);
    }
}
