package ir.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDto {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=])[A-Za-z\\d@#$%^&+=]{8,}$",
            message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character")

    private String password;
}
