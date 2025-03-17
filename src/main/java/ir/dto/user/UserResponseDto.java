package ir.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Double balance;
    private UserStatus status;
//    private byte[] photo;
    private List<CategoryResponseDto> categories;
}
