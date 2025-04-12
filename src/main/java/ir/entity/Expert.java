package ir.entity;

import ir.convert.expert.ByteArrayBlobConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("EXPERT")
@Entity
public class Expert extends User {

    //average rate
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be non-negative")
    @DecimalMax(value = "5.0", inclusive = true, message = "Rating cannot exceed 5.0")
    private Double rating = 0.0;

    @Lob
    @Convert(converter = ByteArrayBlobConverter.class)
    @Column(name = "photo", nullable = true)
    private byte[] photo;

    @OneToMany(mappedBy = "expert")
    private List<Offer> offers;

    @ManyToMany
    @JoinTable(
            name = "expert_category",
            joinColumns = @JoinColumn(name = "expert_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}