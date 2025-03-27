package ir.dto.offer;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for capturing offer request details.
 *
 * <p>This class is used to collect information about an offer, including the order number,
 * proposed price, estimated time to complete the task, and the proposed start time.
 * Validation annotations are applied to ensure the integrity of input data.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>orderNumber</b>: The unique identifier of the order associated with the offer. Must not be null.</li>
 *     <li><b>proposedPrice</b>: The price proposed by the expert for the order. Must be zero or a positive value.</li>
 *     <li><b>estimatedTime</b>: The estimated time (in hours) to complete the order. Must be a positive value.</li>
 *     <li><b>startTime</b>: The proposed start time for the order. Must be in a valid time format.</li>
 * </ul>
 */
@Getter
@Setter
public class OfferRequestDto {

    @NotNull(message = "Order number must not be null")
    private Long orderNumber;

    @PositiveOrZero(message = "Proposed price must be zero or a positive value")
    private BigDecimal proposedPrice;

    @Positive(message = "Estimated time must be a positive value")
    private Integer estimatedTime;

    @NotBlank(message = "Start time must not be blank")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Start time must be in the format HH:mm")
    private String startTime;
}
