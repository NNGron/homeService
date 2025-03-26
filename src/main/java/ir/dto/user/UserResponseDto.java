package ir.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.entity.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing user details in the response layer.
 *
 * <p>This class is used to encapsulate information about a user, including their profile,
 * financial details, categories, and status, while excluding any null values
 * in the serialized JSON output.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link JsonInclude}: Ensures only non-null fields are included in the JSON output.</li>
 *     <li>{@link Builder}: Enables the use of the builder pattern for constructing instances.</li>
 *     <li>{@link Getter} and {@link Setter}: Automatically generates getter and setter methods for the fields.</li>
 * </ul>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>userName</b>: The user's unique username.</li>
 *     <li><b>firstName</b>: The user's first name.</li>
 *     <li><b>lastName</b>: The user's last name.</li>
 *     <li><b>email</b>: The user's email address.</li>
 *     <li><b>balance</b>: The user's current account balance.</li>
 *     <li><b>status</b>: The user's current status (NEW, APPROVED) as defined by {@link UserStatus}.</li>
 *     <li><b>averageRate</b>: The user's average rating, applicable for users such as experts.</li>
 *     <li><b>categories</b>: A list of categories associated with the user, represented by {@link CategoryResponseDto}.</li>
 * </ul>
 *
 * @see CategoryResponseDto
 * @see UserStatus
 */
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private LocalDateTime registrationDate;

    @NotBlank(message = "Username must not be blank")
    private String userName;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @PositiveOrZero(message = "Balance must be zero or a positive value")
    private Double balance;

    @NotNull(message = "Status must not be null")
    private UserStatus status;

    @PositiveOrZero(message = "Average rate must be zero or a positive value")
    private Double averageRate;

    @NotEmpty(message = "Categories must not be empty")
    private List<@NotNull(message = "Category must not be null") CategoryResponseDto> categories;
}
