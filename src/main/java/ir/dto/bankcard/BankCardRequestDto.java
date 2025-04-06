package ir.dto.bankcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BankCardRequestDto {

    private String cardNumber;

    @NotBlank(message = "CardHolderName must not be blank")
    private String cardHolderName;

    private LocalDate expiryDate;

    @Size(min = 3, message = "cvv must have 4 digits")
    @Size(max = 3, message = "cvv must have 4 digits")
    private Integer cvv;

    private String password;

    private BigDecimal balance ;
}
