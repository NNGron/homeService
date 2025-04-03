package ir.exception;

/**
 * Custom exception class for handling specific application errors.
 */
public class CustomException extends RuntimeException {
    private final int errorCode;

    /**
     * Constructs a new CustomException with the specified detail message and error code.
     *
     * @param message   the detail message
     * @param errorCode the error code
     */
    public CustomException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new CustomException with the specified detail message, error code, and cause.
     *
     * @param message   the detail message
     * @param errorCode the error code
     * @param cause     the cause of the exception
     */
    public CustomException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public int getErrorCode() {
        return errorCode;
    }
}