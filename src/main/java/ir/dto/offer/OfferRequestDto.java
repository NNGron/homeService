package ir.dto.offer;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Getter
@Setter
@Validated
public class OfferRequestDto {

    @NotNull(message = "Order number must not be null")
    private Long orderNumber;

    @PositiveOrZero(message = "Proposed price must be zero or a positive value")
    private BigDecimal proposedPrice;

    @Positive(message = "Estimated time must be a positive value")
    private Integer estimatedTime;

    @NotBlank(message = "Start time must not be blank")
    private String availableTime;
}
