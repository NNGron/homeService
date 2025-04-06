package ir.dto.bankcard;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BankCardInfoDto {
    private String cardNumber;
    @Size(max = 3, message = "cvv must have 4 digits")
    private Integer cvv;
    private String password;

}
