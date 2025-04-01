package ir.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class LoginResponseDto {
    private String message;
    private String userName;
    private String userType;
    private LocalDateTime loginTime;
}
