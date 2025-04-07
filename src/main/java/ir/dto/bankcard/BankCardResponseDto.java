package ir.dto.bankcard;

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
public class BankCardResponseDto {

    private String cardNumber;

    private String cardHolderName;

    private LocalDate expiryDate;

    private Integer cvv2;

    private BigDecimal balance ;
}
