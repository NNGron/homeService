package ir.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

@Validated
public record MainCategoryRequestDto(

        @NotBlank(message = "Category name must not be blank")
        @Size(min = 3, max = 100, message = "Category name must be between 3 and 100 characters")
        String name
) {
}
