package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity class representing a review provided by a customer for an order and associated expert.
 *
 * <p>This class extends {@link BaseEntity} and encapsulates review details such as rating,
 * optional comment, and relationships with other entities like {@link Order}, {@link Customer},
 * and {@link Expert}. Validation annotations ensure integrity of the data.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>rating</b>: The rating given in the review, must be between 1 and 5.</li>
 *     <li><b>comment</b>: An optional comment provided with the review, limited to 500 characters.</li>
 *     <li><b>order</b>: The order associated with the review, must not be null.</li>
 *     <li><b>customer</b>: The customer who provided the review, must not be null.</li>
 *     <li><b>expert</b>: The expert associated with the order and review, must not be null.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li><b>rating</b>: Mandatory, must not be null and must be between 1 and 5.</li>
 *     <li><b>comment</b>: Optional, but if provided, must not exceed 500 characters.</li>
 *     <li><b>order</b>: Mandatory, must not be null.</li>
 *     <li><b>customer</b>: Mandatory, must not be null.</li>
 *     <li><b>expert</b>: Mandatory, must not be null.</li>
 * </ul>
 *
 * <p>Relationships:</p>
 * <ul>
 *     <li>{@link OneToOne}: Links the review to a single {@link Order}.</li>
 *     <li>{@link ManyToOne}: Links the review to a {@link Customer} and an {@link Expert}.</li>
 * </ul>
 *
 * @see BaseEntity
 * @see Order
 * @see Customer
 * @see Expert
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review extends BaseEntity {

    @NotNull(message = "Rating must not be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    @Column(name = "comment", length = 500)
    private String comment;

    @NotNull(message = "Order must not be null")
    @OneToOne
    private Order order;

    @NotNull(message = "Customer must not be null")
    @ManyToOne
    private Customer customer;

    @NotNull(message = "Expert must not be null")
    @ManyToOne
    private Expert expert;

}