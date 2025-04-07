package ir.dto.bankcard;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BankCardInfoDto {
    @NotBlank(message = "Card number must not be blank")
    @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
    private String cardNumber;

    @NotBlank(message = "CardHolderName must not be blank")
    private String cardHolderName;

    @Future(message = "Expiry date must be a future date")
    private LocalDate expiryDate;

    @Digits(integer = 3, fraction = 0, message = "CVV2 must have exactly 3 digits")
    private Integer cvv2;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
    private String password;
}
