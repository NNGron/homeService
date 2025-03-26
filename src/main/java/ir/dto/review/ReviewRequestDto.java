package ir.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for capturing review information from users.
 *
 * <p>This class is designed to collect details of a user's review, including the rating,
 * optional comments, and identifiers for the associated order and offer. Validation
 * annotations are applied to ensure the input data adheres to specified rules.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>rating</b>: The user's review rating. Must be an integer between 1 and 5.</li>
 *     <li><b>comment</b>: An optional textual comment for the review, limited to 500 characters.</li>
 *     <li><b>orderNumber</b>: The unique identifier for the order associated with the review.</li>
 *     <li><b>offerNumber</b>: The unique identifier for the offer associated with the review.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li><b>rating</b>: Mandatory, must not be null, and must be between 1 and 5.</li>
 *     <li><b>comment</b>: Optional, but if provided, must not exceed 500 characters.</li>
 *     <li><b>orderNumber</b>: Mandatory, must not be null.</li>
 *     <li><b>offerNumber</b>: Mandatory, must not be null.</li>
 * </ul>
 */
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