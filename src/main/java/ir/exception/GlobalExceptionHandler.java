package ir.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles specific exceptions (e.g., Resource Not Found)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(CustomException ex) {
        ErrorResponseDto responseDto = new ErrorResponseDto(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    /**
     * Handles generic runtime exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value()
                , "An unexpected error occurred");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
