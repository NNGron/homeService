package ir.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the structure of an error response returned by the application.
 *
 * <p>The {@code ErrorResponseDto} class is used to encapsulate error details
 * in a standardized format, making it easy for clients to handle and display
 * error messages. It contains fields for both the error code and the error
 * message, providing necessary context for identifying and debugging issues.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Includes an {@code errorCode} field to represent the unique identifier
 *     of the error, useful for tracking and debugging.</li>
 *     <li>Contains an {@code errorMessage} field to provide a human-readable
 *     description of the error.</li>
 *     <li>Utilizes Lombok annotations ({@link Getter}, {@link Setter},
 *     {@link AllArgsConstructor}, and {@link NoArgsConstructor}) to reduce
 *     boilerplate code.</li>
 * </ul>
 * <p>This class provides a simple and reusable way to represent error responses
 * throughout the application.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    private int errorCode;
    private String errorMessage;
}
