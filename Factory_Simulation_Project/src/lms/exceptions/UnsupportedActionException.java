package lms.exceptions;

/**
 * The UnsupportedActionException class is a type of runtime exception (unchecked exception)
 * that is used to indicate that an unsupported action or operation was attempted.
 */
public class UnsupportedActionException extends RuntimeException {

    /**
     * Constructs a new UnsupportedActionException with no message.
     */
    public UnsupportedActionException() {

    }

    /**
     * Constructs a new UnsupportedActionException with the specified error message.
     * @param message - A String containing the error message to be associated with this exception.
     */

    public UnsupportedActionException(String message) {
        super(message);
    }

    /**
     *Constructs a new UnsupportedActionException with the specified error message and cause.
     * @param message - A String containing the error message to be associated with this exception.
     * @param cause - A Throwable object representing the cause of this exception.
     */
    public UnsupportedActionException(String message,
                                      Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UnsupportedActionException with the specified cause.
     * @param cause - A Throwable object representing the cause of this exception.
     */
    public UnsupportedActionException(Throwable cause) {
        super(cause);
    }

}
