package ir.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtException(JwtException ex) {
        String message = "Invalid token";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.UNAUTHORIZED.getCode(), message);
        return new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        ErrorResponseDto responseDto = new ErrorResponseDto(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(CustomException ex) {
        log.error("Custom exception occurred: {}", ex.getMessage());
        ErrorResponseDto responseDto = new ErrorResponseDto(ex.getErrorCode(), ex.getMessage());
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        String message = "Oops! Something went wrong. Please contact support if the issue persists.";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.SERVER_ERROR.getCode(), message);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingParams(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        String message = "Missing required parameter: '" + ex.getParameterName() + "'";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.BAD_REQUEST.getCode(), message);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidEnumValue(MethodArgumentTypeMismatchException ex) {
        log.error("Parameter type is invalid", ex);
        String message = "Invalid value for parameter '" + ex.getName() + "': '" + ex.getValue() + "'";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.BAD_REQUEST.getCode(), message);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}
