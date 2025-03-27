package ir.dto.user;

import ir.entity.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object (DTO) for capturing user details during user registration or updates.
 *
 * <p>This class serves as a container for user input, with validation annotations to
 * enforce input integrity and ensure valid data is captured at the application level.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>userName</b>: The username of the user. Must not be blank.</li>
 *     <li><b>password</b>: The user's password. Must be at least 8 characters long and include at least one letter and one number.</li>
 *     <li><b>firstName</b>: The user's first name. Must not be blank.</li>
 *     <li><b>lastName</b>: The user's last name. Must not be blank.</li>
 *     <li><b>email</b>: The user's email address. Must be a valid email format and not blank.</li>
 *     <li><b>address</b>: The user's address. Must not exceed 255 characters and must not be blank.</li>
 *     <li><b>credit</b>: The user's credit balance. Must be a non-negative value.</li>
 *     <li><b>subCategoryNames</b>: A list of sub-category names associated with the user (e.g., expert skills). Must not be empty, and each entry must be valid.</li>
 *     <li><b>userType</b>: The type of user (e.g., CUSTOMER or EXPERT). Must not be null.</li>
 * </ul>
 *
 * <p>This DTO is primarily used for:</p>
 * <ul>
 *     <li>User creation requests.</li>
 *     <li>User updates where all details need to be validated and processed.</li>
 * </ul>
 *
 * @see UserType
 */
@Setter
@Getter
public class UserRequestDto {

    @NotBlank(message = "Username must not be blank")
    private String userName;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must have at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must contain at least one letter and one number")
    private String password;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @PositiveOrZero(message = "Credit must be a non-negative value")
    private BigDecimal credit;

    /**
     * A list of sub-category names associated with the user (expert skills).
     * Must not be empty.
     */
    private List<String> subCategoryNames;

    @NotNull(message = "User type is required. Please specify whether the user is a CUSTOMER or an EXPERT.")
    private UserType userType;
}
