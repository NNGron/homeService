package ir.entity;

import ir.convert.ByteArrayBlobConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "experts")
public class Expert extends User {

    /**
     * The expert's rating, must be non-negative and up to 5.0.
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be non-negative")
    @DecimalMax(value = "5.0", inclusive = true, message = "Rating cannot exceed 5.0")
    private Double rating;

    /**
     * The expert's credit, must be non-negative.
     */
    @PositiveOrZero(message = "Credit must be a non-negative value")
    private Double credit;

    /**
     * The expert's photo, must be less than or equal to 300KB.
     */
    @Lob
    @Convert(converter = ByteArrayBlobConverter.class)
    @Column(name = "photo", nullable = true)
    private byte[] photo;


    /**
     * List of offers created by the expert.
     */
    @OneToMany(mappedBy = "expert")
    private List<Offer> offers;

    /**
     * The categories associated with the expert.
     */
    @ManyToMany
    @JoinTable(
            name = "expert_category",
            joinColumns = @JoinColumn(name = "expert_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

}
