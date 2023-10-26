package lms.exceptions;


/**
 * A Special Runtime exception representing a bad game state
 */
public class BadStateException extends RuntimeException {

    /**
     * Constructs a new BadStateException with no message.
     */
    public BadStateException() {}

    /**
     * Constructs a new BadStateException with the specified error message.
     * @param message - A String containing the error message to be associated with this exception.
     */
    public BadStateException(String message) {
        super(message);
    }

    /**
     * Constructs a new BadStateException with the specified detail message and cause.
     * @param message - A String containing the error message to be associated with this exception.
     * @param cause - the cause (which is saved for later retrieval by the getCause() method)
     */

    public BadStateException(String message,
                             Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new BadStateException with the specified cause.
     * @param cause - the cause (which is saved for later retrieval by the getCause() method)
     */

    public BadStateException(Throwable cause) {
        super(cause);
    }

}
