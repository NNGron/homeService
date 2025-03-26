package ir.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for updating a user's password.
 *
 * <p>This class is used to encapsulate the required information for updating a password.
 * It ensures the old password and the new password meet specific validation criteria.</p>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li><b>newPassword</b>:
 *         <ul>
 *             <li>Must not be blank.</li>
 *             <li>Must be at least 8 characters long.</li>
 *             <li>Must contain at least one letter and one number for security.</li>
 *         </ul>
 *     </li>
 *     <li><b>oldPassword</b>: Must not be blank.</li>
 * </ul>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>newPassword</b>: The user's new password.</li>
 *     <li><b>oldPassword</b>: The user's current password.</li>
 * </ul>
 */
public record UserUpdatePasswordDto(

        @NotBlank(message = "New password must not be blank")
        @Size(min = 8, message = "New password must have at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "New password must contain at least one letter and one number"
        )
        String newPassword,

        @NotBlank(message = "Old password must not be blank")
        String oldPassword) {
}
