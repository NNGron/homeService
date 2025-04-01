package ir.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private Integer rating;
    private String comment;
    private Long orderNumber;
    private String orderDescription;
    private String customerFirstName;
    private String customerLastName;
    private String expertFirstName;
    private String expertLastName;
    private String subCategoryName;
}