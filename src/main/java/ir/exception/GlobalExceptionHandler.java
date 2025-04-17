package ir.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

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
        // جمع‌آوری تمام پیام‌های خطا برای فیلدهای نامعتبر
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        // ساخت پاسخ خطا
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.BAD_REQUEST.getCode(), errorMessage);

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
//        //String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
//        String errorMessage = ex.getBindingResult().getFieldError() != null
//                ? ex.getBindingResult().getFieldError().getDefaultMessage()
//                : "Invalid input data.";
//        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.BAD_REQUEST.getCode(), errorMessage);
////        return new ResponseEntity<>(responseDto, HttpStatus.OK);
//        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
//    }

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Database constraint violation: {}", ex.getMessage(), ex);

        // بررسی متن خطا برای محدودیت خاص ایمیل
        if (ex.getMessage().contains("users_email_key")) {
            String message = "The provided email address already exists. Please use a different email.";
            ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.ALREADY_EXIST.getCode(), message);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }

        // بررسی متن خطا برای محدودیت خاص یوزرنیم
        if (ex.getMessage().contains("users_user_name_key")) {
            String message = "The provided username already exists. Please choose another username.";
            ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.ALREADY_EXIST.getCode(), message);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }

        // پیام عمومی برای سایر محدودیت‌های دیتابیس
        String message = "A database constraint was violated. Please check your input.";
        ErrorResponseDto responseDto = new ErrorResponseDto(ErrorCode.BAD_REQUEST.getCode(), message);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}