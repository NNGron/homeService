package ir.dto.user;

import ir.entity.enums.UserType;
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
    private String address;
    private Double credit;
    private String skills;
    private UserType userType;
    private byte[] photo;
}
