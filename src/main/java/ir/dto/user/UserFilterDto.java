package ir.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * A Data Transfer Object (DTO) used for filtering user data based on specific attributes.
 *
 * <p>This class provides a structured representation of filtering criteria for users.
 * It is implemented as a record.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Contains attributes such as first name, last name, email, and associated categories.</li>
 *     <li>Includes validation constraints for ensuring input data integrity.</li>
 *     <li>Used in service-layer logic or controllers for user-related filtering operations.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li>{@code firstName} - Must not be blank.</li>
 *     <li>{@code lastName} - Must not be blank.</li>
 *     <li>{@code email} - Must not be blank and must follow a valid email format.</li>
 *     <li>{@code categoryNames} - A list of category names, each of which must not be blank.</li>
 * </ul>
 *
 * @param firstName     The first name of the user. Must not be blank.
 * @param lastName      The last name of the user. Must not be blank.
 * @param email         The email address of the user. Must not be blank and must follow a valid email format.
 * @param categoryNames A list of category names associated with the user. Each name in the list must not be blank.
 */
public record UserFilterDto(

        @NotBlank(message = "First name must not be blank")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        String lastName,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        String email,

        List<@NotBlank(message = "Category name must not be blank") String> categoryNames) {
}
