package ir.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    private LocalDateTime registrationDate;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal balance;
    private UserStatus status;
    private Double averageRate;
    private List<CategoryResponseDto> categories;
}
