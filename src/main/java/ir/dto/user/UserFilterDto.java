package ir.dto.user;

import ir.entity.enums.UserType;

import java.util.List;

public record UserFilterDto(
        UserType userType,
        Double rating,
        String firstName,
        String lastName,
        String email,
        List<String> categoryNames) {
}