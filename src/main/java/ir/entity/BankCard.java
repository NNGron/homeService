package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

//@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_card")
public class BankCard extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true) // تضمین یکتا بودن user_id
    private User owner;

    @Column(unique = true, nullable = false) // تضمین یکتا بودن و مقداردهی غیر null
    @NotBlank(message = "Card number must not be blank")
//    @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
    private String cardNumber;

    @NotBlank(message = "CardHolderName must not be blank")
    private String cardHolderName;

    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @Digits(integer = 3, fraction = 0, message = "CVV2 must have exactly 3 digits")
    private Integer cvv2;

    @NotBlank(message = "Password must not be blank")
    private String password;

    private BigDecimal balance = BigDecimal.ZERO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankCard bankCard = (BankCard) o;
        return Objects.equals(cardNumber, bankCard.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }
}
