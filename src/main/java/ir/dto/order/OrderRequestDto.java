package ir.dto.order;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Validated
public class OrderRequestDto {

    @NotBlank(message = "Main Category name must not be null")
    @Size(max = 100, message = "Main Category name must not exceed 100 characters")
    private String mainCategoryName;

    @NotBlank(message = "SubCategory name must not be null")
    @Size(max = 100, message = "SubCategory name must not exceed 100 characters")
    private String subCategoryName;

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
}
