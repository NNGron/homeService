package ir.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.entity.Offer;
import ir.entity.Review;
import ir.entity.enums.OrderStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {


    /**
     * The unique order number (must not be null).
     */
    @NotNull(message = "Order number cannot be null")
    private Long orderNumber;

    /**
     * The final price of the order (must be positive if present).
     */
    @PositiveOrZero(message = "Final price must be zero or a positive value")
    private Double finalPrice;

    /**
     * The date the order was placed (must not be null).
     */
    @NotNull(message = "Order date cannot be null")
    private LocalDateTime orderDate;

    /**
     * A brief description of the order.
     */
    private String orderDescription;

    /**
     * The name of the order (must not be blank).
     */
    @NotBlank(message = "Order name cannot be blank")
    private String orderName;

    /**
     * The expected execution date of the order.
     */
    @Future(message = "Execution date must be in the future")
    private LocalDateTime executionDate;

    /**
     * The address for the order (must not be blank).
     */
    @NotBlank(message = "Order address cannot be blank")
    private String orderAddress;

    /**
     * The category information for the order.
     */
    private CategoryResponseDto categoryResponseDto;

    /**
     * The current status of the order.
     */
    @NotNull(message = "Order status cannot be null")
    private OrderStatus status;

    /**
     * The list of offers associated with the order.
     */
    private List<Offer> offers;

    /**
     * The review associated with the order.
     */
    private Review review;
}
