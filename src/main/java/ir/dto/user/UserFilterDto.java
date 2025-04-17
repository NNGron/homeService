package ir.dto.user;

import ir.entity.enums.UserType;

import java.time.LocalDateTime;
import java.util.List;

public record UserFilterDto(
        UserType userType,
        Double rating,
        String firstName,
        String lastName,
        String email,
        List<String> categoryNames,
        LocalDateTime registrationStartDate,
        String subCategoryName
) {
}
