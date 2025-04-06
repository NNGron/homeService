package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankCard extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(unique = true)
    private String cardNumber;

    @NotBlank(message = "CardHolderName must not be blank")
    private String cardHolderName;

    private LocalDate expiryDate;

    @Size(min = 3, message = "cvv must have 4 digits")
    @Size(max = 3, message = "cvv must have 4 digits")
    private Integer cvv;

    private String password;

    private BigDecimal balance = BigDecimal.ZERO;
}
