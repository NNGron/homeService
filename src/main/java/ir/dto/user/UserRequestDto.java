package ir.dto.user;

import ir.entity.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserRequestDto {

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;

    private String firstName;

    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String address;

    @PositiveOrZero(message = "Credit must be non-negative")
    private Double credit;

    private List<String> subCategoryNames; // Expert Skills

    @NotNull(message = "User type is required")
    private UserType userType;
}
