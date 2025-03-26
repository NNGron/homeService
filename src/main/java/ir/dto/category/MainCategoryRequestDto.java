package ir.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

/**
 * Data Transfer Object (DTO) for capturing details of a main category request.
 *
 * <p>This class encapsulates the necessary information for creating or updating
 * a main category, specifically its name. Validation annotations ensure the integrity
 * and accuracy of the input data.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>name</b>: The name of the main category. Must not be blank, must be at least 3 characters long, and must not exceed 100 characters.</li> * </ul>
 */
@Validated
public record MainCategoryRequestDto(

        @NotBlank(message = "Category name must not be blank")
        @Size(min = 3, max = 100, message = "Category name must be between 3 and 100 characters")
        String name
) {
}
