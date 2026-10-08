package util;

import exception.TransactionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC Transaction Manager providing atomic transaction boundary management,
 * auto-commit toggle, commit, rollback, and connection cleanup.
 */
public class TransactionManager {

    private static final Logger LOGGER = Logger.getLogger(TransactionManager.class.getName());

    private TransactionManager() {}

    /**
     * Executes a callback within an atomic JDBC transaction boundary.
     * Automatically handles setAutoCommit(false), commit(), rollback(), and connection release.
     *
     * @param callback Operations to execute inside transaction
     * @param <T> Return type
     * @return Execution result
     * @throws TransactionException if transaction fails and is rolled back
     */
    public static <T> T executeInTransaction(TransactionCallback<T> callback) throws TransactionException {
        if (callback == null) {
            throw new TransactionException("TransactionCallback cannot be null.");
        }

        Connection connection = null;
        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false);
            LOGGER.info("[TRANSACTION] Started JDBC transaction boundary on connection: " + connection.hashCode());

            T result = callback.doInTransaction(connection);

            connection.commit();
            LOGGER.info("[TRANSACTION] Transaction committed successfully. Connection: " + connection.hashCode());
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[TRANSACTION] Exception inside transaction boundary! Initiating ROLLBACK. Error: " + e.getMessage(), e);
            if (connection != null) {
                try {
                    connection.rollback();
                    LOGGER.info("[TRANSACTION] Transaction ROLLBACK executed successfully.");
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "[TRANSACTION] Error during transaction rollback: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw new TransactionException("Transaction failed and was rolled back: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        connection.setAutoCommit(true);
                        connection.close();
                        LOGGER.info("[TRANSACTION] Connection closed and returned to pool.");
                    }
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.WARNING, "[TRANSACTION] Error closing transaction connection: " + closeEx.getMessage(), closeEx);
                }
            }
        }
    }
}
