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

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
    private BigDecimal proposedPrice;

    @NotNull(message = "Estimated time must not be null")
    @Positive(message = "Estimated time must be a positive value")
    @Column(name = "estimated_time", nullable = false)
    private Integer estimatedTime;

    @NotBlank(message = "Start time must not be blank")
    @Column(name = "available_time", length = 50, nullable = false)
    private String availableTime;
}