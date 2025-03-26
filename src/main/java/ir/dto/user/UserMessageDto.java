package ir.dto.user;

/**
 * A Data Transfer Object (DTO) representing a user-related message.
 *
 * <p>This class is used to encapsulate messages related to user operations,
 * such as success or error notifications, in a structured format.</p>
 *
 * @param message The message string providing details about the operation or notification.
 */
public record UserMessageDto(String message) {
}
