package ir.entity;

import ir.convert.expert.ByteArrayBlobConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.util.List;

/**
 * Represents an expert within the system.
 *
 * <p>The {@code Expert} class extends the {@link User} class and provides
 * additional fields and relationships specific to experts. Experts are associated
 * with offers they create and the categories they specialize in. This class also
 * stores their rating and photo for further personalization.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Inherits basic user details like name, email, and credentials from {@link User}.</li>
 *     <li>Maintains a {@code rating} field to represent the expert's performance, which must
 *     be a non-negative value and cannot exceed 5.0.</li>
 *     <li>Supports a photo field to store the expert's profile picture, with a maximum size
 *     of 300KB, using {@link ByteArrayBlobConverter} for storage.</li>
 *     <li>Associates with the {@link Offer} entity to represent the offers created by the expert.</li>
 *     <li>Uses a many-to-many relationship with {@link Category} to define areas of expertise.</li>
 * </ul>
 *
 * <p>Relationships:</p>
 * <ul>
 *     <li>{@code offers} - A one-to-many relationship representing all offers made by the expert.</li>
 *     <li>{@code categories} - A many-to-many relationship defining the categories the expert specializes in.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li>{@code rating} - Must be between 0.0 and 5.0, inclusive.</li>
 *     <li>{@code photo} - Optional, but must adhere to the size limit of 300KB.</li>
 * </ul>
 *
 * <p>This class is crucial for modeling and managing experts within the application,
 * enabling a clear representation of their skills, ratings, and contributions.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "experts")
public class Expert extends User {

    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be non-negative")
    @DecimalMax(value = "5.0", inclusive = true, message = "Rating cannot exceed 5.0")
    private Double rating;

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
