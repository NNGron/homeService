package ir.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

@Validated
public record ReviewRequestDto(

        @NotNull(message = "Rating must not be null")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating,

        @Size(max = 500, message = "Comment must not exceed 500 characters")
        String comment,

        @NotNull(message = "Order number must not be null")
        Long orderNumber,

        @NotNull(message = "Offer number must not be null")
        Long offerNumber
) {
}