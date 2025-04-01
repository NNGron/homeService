package ir.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.entity.Offer;
import ir.entity.Review;
import ir.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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

    private Long orderNumber;
    private BigDecimal finalPrice;
    private LocalDate orderDate;
    private String orderDescription;
    private String orderName;
    private LocalDate executionDate;
    private String orderAddress;
    private CategoryResponseDto categoryResponseDto;
    private OrderStatus status;
    private List<Offer> offers;
    private Review review;
    private BigDecimal offerCustomerPrice;
}
