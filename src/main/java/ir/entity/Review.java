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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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