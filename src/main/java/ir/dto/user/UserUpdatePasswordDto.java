package ir.dto.user;

public record UserUpdatePasswordDto(
        String newPassword,
        String oldPassword) {
}
