package ir.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.entity.Offer;
import ir.entity.Review;
import ir.entity.enums.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing order details in the response layer.
 *
 * <p>This class encapsulates necessary information about an order, such as its identifier,
 * pricing, dates, status, and associated entities like offers and reviews. Validation
 * annotations are applied to ensure data integrity.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link JsonIgnoreProperties}: Ignores any unknown JSON properties during deserialization.</li>
 *     <li>{@link JsonInclude}: Ensures only non-null fields are included in the JSON response.</li>
 *     <li>{@link Getter} and {@link Setter}: Automatically generates getter and setter methods for all fields.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {

    @NotNull(message = "Order number cannot be null")
    private Long orderNumber;

    @PositiveOrZero(message = "Final price must be zero or a positive value")
    private Double finalPrice;

    @NotNull(message = "Order date cannot be null")
    private LocalDate orderDate;

    @NotBlank(message = "Order description cannot be blank")
    @Size(max = 500, message = "Order description must not exceed 500 characters")
    private String orderDescription;

    @NotBlank(message = "Order name cannot be blank")
    @Size(max = 100, message = "Order name must not exceed 100 characters")
    private String orderName;

    @NotNull(message = "Execution date cannot be null")
    @Future(message = "Execution date must be in the future")
    private LocalDate executionDate;

    @NotBlank(message = "Order address cannot be blank")
    @Size(max = 255, message = "Order address must not exceed 255 characters")
    private String orderAddress;

    private CategoryResponseDto categoryResponseDto;

    @NotNull(message = "Order status cannot be null")
    private OrderStatus status;

    @Size(min = 1, message = "Offers list must not be empty")
    private List<Offer> offers;

    private Review review;

    private Double offerCustomerPrice;
}
