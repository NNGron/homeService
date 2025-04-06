package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @PositiveOrZero(message = "Credit must be a non-negative value")
    private BigDecimal credit = BigDecimal.ZERO;
}
