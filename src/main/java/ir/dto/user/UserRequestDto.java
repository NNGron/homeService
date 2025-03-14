package ir.dto.user;

import ir.entity.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequestDto {
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
