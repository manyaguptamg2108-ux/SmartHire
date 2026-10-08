package exception;

public class InvalidJobException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidJobException(String message) {
        super(message);
    }

    public InvalidJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
