package ir.dto.offer;

import ir.dto.order.OrderResponseDto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for representing offer details in the response layer.
 *
 * <p>This class encapsulates information about an offer, including its unique identifier,
 * associated order details, expert information, proposed pricing, estimated time, and
 * proposed start time. It is primarily used to structure and return offer-related data
 * in a standardized format.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>offerNumber</b>: A unique identifier for the offer.</li>
 *     <li><b>order</b>: The associated order details, represented as an {@link OrderResponseDto}.</li>
 *     <li><b>expertName</b>: The name of the expert providing the offer.</li>
 *     <li><b>expertEmail</b>: The email address of the expert providing the offer.</li>
 *     <li><b>proposedPrice</b>: The price proposed by the expert for completing the order.</li>
 *     <li><b>estimatedTime</b>: The estimated time, in hours, to complete the order.</li>
 *     <li><b>startTime</b>: The proposed start time for the order.</li>
 * </ul>
 */
@Getter
@Setter
public class OfferResponseDto {
    @NotNull(message = "Offer number must not be null")
    private Long offerNumber;

    @NotNull(message = "Order details must not be null")
    private OrderResponseDto order;

    @NotBlank(message = "Expert name must not be blank")
    @Size(max = 100, message = "Expert name must not exceed 100 characters")
    private String expertName;

    @NotBlank(message = "Expert email must not be blank")
    @Email(message = "Expert email must be a valid email address")
    private String expertEmail;

    @PositiveOrZero(message = "Proposed price must be zero or a positive value")
    private Double proposedPrice;

    @Positive(message = "Estimated time must be a positive value")
    private Integer estimatedTime;

    @NotBlank(message = "Start time must not be blank")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Start time must be in the format HH:mm")
    private String startTime;
}
