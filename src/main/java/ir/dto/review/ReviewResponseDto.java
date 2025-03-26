package ir.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for encapsulating review-related response data.
 *
 * <p>This class provides details about a review, including rating, comment, order information,
 * customer and expert details, and subcategory information. It uses {@link JsonInclude}
 * to ensure only non-null fields are included in the serialized JSON response.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link AllArgsConstructor}: Automatically generates an all-arguments constructor.</li>
 *     <li>{@link Builder}: Enables the builder pattern for constructing instances.</li>
 *     <li>{@link Getter} and {@link Setter}: Automatically generates getter and setter methods.</li>
 *     <li>{@link JsonInclude}: Excludes null fields from the JSON response.</li>
 * </ul>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>rating</b>: The rating provided for the review, ranging from 1 to 5.</li>
 *     <li><b>comment</b>: An optional comment provided by the reviewer.</li>
 *     <li><b>orderNumber</b>: The unique identifier of the order associated with the review.</li>
 *     <li><b>orderDescription</b>: A brief description of the order reviewed.</li>
 *     <li><b>customerFirstName</b>: The first name of the customer who placed the order.</li>
 *     <li><b>customerLastName</b>: The last name of the customer who placed the order.</li>
 *     <li><b>expertFirstName</b>: The first name of the expert associated with the order.</li>
 *     <li><b>expertLastName</b>: The last name of the expert associated with the order.</li>
 *     <li><b>subCategoryName</b>: The subcategory name related to the expertise or order.</li>
 * </ul>
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponseDto {

    @NotNull(message = "Rating must not be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;

    @NotNull(message = "Order number must not be null")
    private Long orderNumber;

    @NotBlank(message = "Order description must not be blank")
    @Size(max = 500, message = "Order description must not exceed 500 characters")
    private String orderDescription;

    @NotBlank(message = "Customer first name must not be blank")
    private String customerFirstName;

    @NotBlank(message = "Customer last name must not be blank")
    private String customerLastName;

    @NotBlank(message = "Expert first name must not be blank")
    private String expertFirstName;

    @NotBlank(message = "Expert last name must not be blank")
    private String expertLastName;

    @NotBlank(message = "Subcategory name must not be blank")
    private String subCategoryName;
}