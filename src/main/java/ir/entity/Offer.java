package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

/**
 * Represents an offer made by an expert for a specific order.
 *
 * <p>The {@code Offer} class links an {@link Expert} with an {@link Order}, providing
 * details about the expert's proposed price, estimated time, and start time for
 * fulfilling the order. This entity acts as a bridge between experts and customers,
 * facilitating the negotiation and selection process for completing an order.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Associates each offer with a specific {@link Order} and {@link Expert} through
 *     {@code ManyToOne} relationships.</li>
 *     <li>Includes fields for proposed price, estimated time, and start time, each
 *     validated with constraints to ensure data integrity.</li>
 *     <li>Extends {@link BaseEntity}, inheriting the primary key field {@code id}.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li>{@code order} - Must not be null. Links the offer to a specific order.</li>
 *     <li>{@code expert} - Must not be null. Links the offer to a specific expert.</li>
 *     <li>{@code proposedPrice} - Must be zero or a positive value.</li>
 *     <li>{@code estimatedTime} - Must be a positive integer, indicating the time required in hours.</li>
 *     <li>{@code startTime} - Must not be blank, specifying when the order work is expected to begin.</li>
 * </ul>
 *
 * <p>Relationships:</p>
 * <ul>
 *     <li>{@code order} - Many-to-one relationship with the {@link Order} entity.</li>
 *     <li>{@code expert} - Many-to-one relationship with the {@link Expert} entity.</li>
 * </ul>
 *
 * <p>This class is essential for modeling the interaction between customers and
 * experts, providing a structured way to manage offers for orders.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Offer extends BaseEntity {

    @NotNull(message = "Order must not be null")
    @ManyToOne
    private Order order;

    @NotNull(message = "Expert must not be null")
    @ManyToOne
    private Expert expert;

    @PositiveOrZero(message = "Proposed price must be zero or a positive value")
    @Column(name = "proposed_price", nullable = false)
    private Double proposedPrice;

    @NotNull(message = "Estimated time must not be null")
    @Positive(message = "Estimated time must be a positive value")
    @Column(name = "estimated_time", nullable = false)
    private Integer estimatedTime;

    @NotBlank(message = "Start time must not be blank")
    @Column(name = "start_time", length = 50, nullable = false)
    private String startTime;
}