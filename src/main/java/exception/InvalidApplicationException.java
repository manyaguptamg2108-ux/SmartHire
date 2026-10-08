package exception;

public class InvalidApplicationException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidApplicationException(String message) {
        super(message);
    }

    public InvalidApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
