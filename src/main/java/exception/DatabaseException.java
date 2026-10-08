package exception;

/**
 * Custom exception representing JDBC and Database persistence errors.
 * Preserves the underlying SQLException cause.
 */
public class DatabaseException extends Exception {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
