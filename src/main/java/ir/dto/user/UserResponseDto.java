package ir.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String skills;
}
