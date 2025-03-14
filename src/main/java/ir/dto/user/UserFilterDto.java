package ir.dto.user;

public record UserFilterDto(
        String firstName,
        String lastName,
        String email,
        String skills) {
}
