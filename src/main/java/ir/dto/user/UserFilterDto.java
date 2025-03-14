package ir.dto.user;

import ir.entity.enums.Role;
public record UserFilterDto(
        String userName,
        String password,
        String firstName,
        String lastName,
        String email,
        Role role) {
}
