package ir.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for capturing details of a subcategory request.
 *
 * <p>This class encapsulates the necessary information for creating or updating
 * a subcategory, including its name, description, base price, and parent category name.
 * Validation annotations are applied to ensure integrity and accuracy of the input data.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>name</b>: The name of the subcategory. Must not be blank and must not exceed 100 characters.</li>
 *     <li><b>description</b>: A detailed description of the subcategory. Optional but limited to 255 characters.</li>
 *     <li><b>basePrice</b>: The base price associated with the subcategory. Must be zero or a positive value.</li>
 *     <li><b>parentName</b>: The name of the parent category for the subcategory. Must not be blank and must not exceed 100 characters.</li>
 * </ul>
 */
public record SubCategoryRequestDto(
        @NotBlank(message = "Subcategory name must not be blank")
        @Size(max = 100, message = "Subcategory name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Description must not be blank")
        @Size(max = 255, message = "Subcategory description must not exceed 255 characters")
        String description,

        @NotNull(message = "Base Price must not be blank")
        @PositiveOrZero(message = "Base price must be zero or a positive value")
        Double basePrice,

        @NotBlank(message = "Parent category name must not be blank")
        @Size(max = 100, message = "Parent category name must not exceed 100 characters")
        String parentName
) {
}
