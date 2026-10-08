package exception;

/**
 * Custom Exception thrown when a JDBC transaction fails and is rolled back.
 */
public class TransactionException extends Exception {

    private static final long serialVersionUID = 1L;

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
