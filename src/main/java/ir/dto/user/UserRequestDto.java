package ir.dto.user;

import ir.entity.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class UserRequestDto {

    @NotBlank(message = "Username must not be blank")
    private String userName;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=])[A-Za-z\\d@#$%^&+=]{8,}$",
            message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character")

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
