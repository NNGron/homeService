package ir.dto.user;

import ir.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for Login Response.
 * Contains a login success message, the user's first name, and their role.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String message;   // پیام موفقیت لاگین
    private String username;
    private String token; // token
}

