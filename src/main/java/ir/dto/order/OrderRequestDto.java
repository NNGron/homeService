package ir.dto.order;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for capturing order request details.
 *
 * <p>This class is used to collect order-related information such as pricing, dates,
 * descriptions, and addresses. Validation annotations ensure that input data
 * complies with the required business rules and constraints.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>offerCustomerPrice</b>: The price offered by the customer, must be non-negative.</li>
 *     <li><b>orderDate</b>: The date and time when the order is placed, must not be null.</li>
 *     <li><b>orderDescription</b>: A detailed description of the order, must not be blank or exceed 500 characters.</li>
 *     <li><b>executionDate</b>: The scheduled execution date and time for the order, must not be in the past.</li>
 *     <li><b>orderAddress</b>: The address where the order will be executed, must not exceed 255 characters if provided.</li>
 *     <li><b>subCategoryName</b>: The subcategory associated with the order, must not exceed 100 characters if provided.</li>
 * </ul>
 */
@Getter
@Setter
public class OrderRequestDto {

    private BigDecimal offerCustomerPrice;

    @NotNull(message = "Order date must not be null")
    @FutureOrPresent(message = "Order date must not be in the past")
    private LocalDate orderDate;

    @NotBlank(message = "Description must not be null")
    private String orderDescription;

    @NotNull(message = "Execution date must not be null")
    @FutureOrPresent(message = "Execution date must not be in the past")
    private LocalDate executionDate;

    @NotBlank(message = "Order address must not be null")
    @Size(max = 255, message = "Order address must not exceed 255 characters")
    private String orderAddress;

    @NotBlank(message = "SubCategory name must not be null")
    @Size(max = 100, message = "Subcategory name must not exceed 100 characters")
    private String subCategoryName;
}
