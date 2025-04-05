package ir.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Centralized exception handler for the application.
 *
 * <p>This class provides a unified mechanism to handle various types of exceptions
 * that occur in the application and ensures consistent error responses. It uses
 * Spring's {@link RestControllerAdvice} to intercept and process exceptions thrown
 * by REST controllers.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Handles {@link CustomException} to manage application-specific errors.</li>
 *     <li>Manages global exceptions using {@link Exception}, returning generic error responses.</li>
 *     <li>Processes validation errors caused by invalid arguments via {@link MethodArgumentNotValidException}.</li>
 *     <li>Handles missing parameters in requests using {@link MissingServletRequestParameterException}.</li>
 *     <li>Resolves type mismatches in method arguments with {@link MethodArgumentTypeMismatchException}.</li>
 * </ul>
 *
 * <p>Error Response Format:</p>
 * <ul>
 *     <li>All methods return a structured {@link ErrorResponseDto} containing the error code and message.</li>
 *     <li>The HTTP status code is set to 200 (OK) to align with the application's error-handling design.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <ul>
 *     <li>Automatically invoked by Spring when exceptions occur in REST controller methods.</li>
 *     <li>Provides consistent error handling across the application.</li>
 * </ul>
 *
 * @see RestControllerAdvice
 * @see CustomException
 * @see ErrorResponseDto
 * @see MethodArgumentNotValidException
 * @see MissingServletRequestParameterException
 * @see MethodArgumentTypeMismatchException
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom exceptions that occur in the application.
     *
     * <p>This method is triggered whenever a {@link CustomException} is thrown.
     * It captures the error message and error code from the exception and
     * creates an {@link ErrorResponseDto} object containing these details.</p>
     *
     * <p>The method logs the error for debugging purposes and then returns
     * a structured error response to the client with an HTTP status of 200 (OK).</p>
     *
     * @param ex The {@link CustomException} instance that contains the error message and error code.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponseDto} with the error details.
     * The HTTP status code in the response is 200 (OK).
     * @see CustomException
     * @see ErrorResponseDto
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(CustomException ex) {
        log.error("Custom exception occurred: {}", ex.getMessage());
        ErrorResponseDto responseDto = new ErrorResponseDto(ex.getErrorCode(), ex.getMessage());
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);  // حالا کد 400 برمی‌گردد
    }

    /**
     * Handles unexpected and unhandled exceptions globally across the application.
     *
     * <p>This method is invoked whenever an unhandled exception occurs in the application.
     * It logs the details of the error for debugging purposes and provides a user-friendly
     * error message to the client. The error response includes a generic error message
     * and a server error code.</p>
     *
     * <p>Key Features:</p>
     * <ul>
     *     <li>Logs the exception details, including the stack trace, for easier debugging.</li>
     *     <li>Returns a structured {@link ErrorResponseDto} containing a custom error message
     *     and the appropriate error code.</li>
     *     <li>Ensures the response follows a consistent error-handling format.</li>
     * </ul>
     *
     * @param ex The {@link Exception} that caused the error.
     * @return A {@link ResponseEntity} containing the {@link ErrorResponseDto} with the error details.
     * The HTTP status in the response is 200 (OK) for consistency, despite the error.
     * @see ErrorResponseDto
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        String message = "Oops! Something went wrong. Please contact support if the issue persists.";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.SERVER_ERROR.getCode(), message);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation exceptions caused by invalid method arguments.
     *
     * <p>This method is triggered whenever a {@link MethodArgumentNotValidException} is thrown.
     * It extracts the first validation error message from the exception details and constructs
     * an {@link ErrorResponseDto} containing this error message along with a custom error code.</p>
     *
     * <p>The response is returned with an HTTP status of 200 (OK) for consistency with the
     * application's error-handling approach.</p>
     *
     * <p>Key Features:</p>
     * <ul>
     *     <li>Extracts the first validation error message from the {@code BindingResult}.</li>
     *     <li>Creates a structured response containing the error code and message.</li>
     *     <li>Ensures a user-friendly error message is provided to the client.</li>
     * </ul>
     *
     * @param ex The {@link MethodArgumentNotValidException} containing details about the validation failure.
     * @return A {@link ResponseEntity} containing the {@link ErrorResponseDto} with the validation error message.
     * The HTTP status in the response is 200 (OK).
     * @see MethodArgumentNotValidException
     * @see ErrorResponseDto
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        //String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        String errorMessage = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "Invalid input data.";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.BAD_REQUEST.getCode(), errorMessage);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles cases where a required request parameter is missing from the client request.
     *
     * <p>This method is triggered when a {@link MissingServletRequestParameterException} occurs.
     * It logs the missing parameter's name for debugging purposes and constructs an
     * {@link ErrorResponseDto} with a descriptive error message and a custom error code.</p>
     *
     * <p>The client is informed about the missing parameter in a user-friendly manner
     * to assist in correcting the request. The response is returned with an HTTP status
     * of 200 (OK), as per the application's error-handling standard.</p>
     *
     * <p>Key Features:</p>
     * <ul>
     *     <li>Logs the name of the missing request parameter for easier debugging.</li>
     *     <li>Returns a structured error response with an error code and a detailed message.</li>
     *     <li>Encourages the client to include the missing parameter and try again.</li>
     * </ul>
     *
     * @param ex The {@link MissingServletRequestParameterException} containing details about the missing parameter.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponseDto} with the error details and message.
     * The HTTP status in the response is 200 (OK).
     * @see MissingServletRequestParameterException
     * @see ErrorResponseDto
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingParams(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        String message = "Missing required parameter: '" + ex.getParameterName() + "'";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.BAD_REQUEST.getCode(), message);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions when a method argument has an invalid type or value.
     *
     * <p>This method is triggered when a {@link MethodArgumentTypeMismatchException} occurs,
     * which typically happens when an argument value in a client request cannot be converted
     * to the expected type (e.g., an invalid enum value or a type mismatch).</p>
     *
     * <p>The method logs the error details for debugging purposes and constructs an
     * {@link ErrorResponseDto} containing a user-friendly error message and a custom
     * error code. The response is returned with an HTTP status of 200 (OK) for consistency.</p>
     *
     * @param ex The {@link MethodArgumentTypeMismatchException} containing details about the type mismatch error.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponseDto} with the error details.
     * The HTTP status in the response is 200 (OK).
     * @see MethodArgumentTypeMismatchException
     * @see ErrorResponseDto
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidEnumValue(MethodArgumentTypeMismatchException ex) {
        log.error("Parameter type is invalid", ex);
        String message = "Invalid value for parameter '" + ex.getName() + "': '" + ex.getValue() + "'";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.BAD_REQUEST.getCode(), message);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}
