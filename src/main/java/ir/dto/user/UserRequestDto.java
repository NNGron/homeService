package ir.dto.user;

import ir.entity.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private List<String> subCategoryNames;//Expert Skills
    private UserType userType;
    private byte[] photo;
}
