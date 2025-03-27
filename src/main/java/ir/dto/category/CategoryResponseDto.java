package ir.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.order.OrderResponseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing category details in the response layer.
 *
 * <p>This class encapsulates information about a category, including its parent category,
 * name, description, pricing details, and associated orders. Validation annotations are
 * applied to ensure integrity and accuracy of the data.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link JsonInclude}: Ensures only non-null fields are included in the serialized JSON response.</li>
 *     <li>{@link Builder}: Enables the use of the builder pattern for constructing instances.</li>
 *     <li>{@link Getter} and {@link Setter}: Automatically generates getter and setter methods for all fields.</li>
 *     <li>{@link AllArgsConstructor} and {@link NoArgsConstructor}: Generates constructors for convenience.</li>
 * </ul>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>name</b>: The name of the category. Must not be blank and must not exceed 100 characters.</li>
 *     <li><b>description</b>: A detailed description of the category. Optional but limited to 255 characters.</li>
 *     <li><b>basePrice</b>: The base price associated with the category. Must be zero or a positive value.</li>
 *     <li><b>orders</b>: The list of orders associated with the category, represented as {@link OrderResponseDto} objects.</li>
 *     <li><b>parentCategory</b>: The parent category for the current category. Must not be null.</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDto {

    @NotBlank(message = "Category name must not be blank")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Category description must not exceed 255 characters")
    private String description;

    @PositiveOrZero(message = "Base price must be zero or a positive value")
    private BigDecimal basePrice;

    private List<OrderResponseDto> orders;

    private CategoryResponseDto parentCategory;
}
