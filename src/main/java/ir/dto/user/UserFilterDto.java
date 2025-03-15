package ir.dto.user;

import java.util.List;

public record UserFilterDto(
        String firstName,
        String lastName,
        String email,
        List<String> categoryNames) {
}
