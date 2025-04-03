package ir.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
public record SubCategoryRequestDto(
        @NotBlank(message = "Subcategory name must not be blank")
        @Size(max = 100, message = "Subcategory name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Description must not be blank")
        @Size(max = 255, message = "Subcategory description must not exceed 255 characters")
        String description,

        @NotNull(message = "Base Price must not be blank")
        @PositiveOrZero(message = "Base price must be zero or a positive value")
        BigDecimal basePrice,

        @NotBlank(message = "Parent category name must not be blank")
        @Size(max = 100, message = "Parent category name must not exceed 100 characters")
        String parentName
) {
}
