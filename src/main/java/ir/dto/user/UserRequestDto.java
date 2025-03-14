package ir.dto.user;

import ir.entity.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequestDto {
    private String userName;
    @NotBlank
    private String password;
    private String firstName;
    private String lastName;
    @NotBlank(message = "Email should be not null")
    private String email;
    private String address;
    private Double credit;
    private String skills;
    private UserType userType;
    private byte[] photo;
}
