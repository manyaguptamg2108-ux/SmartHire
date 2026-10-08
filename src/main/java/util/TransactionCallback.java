package util;

import java.sql.Connection;

/**
 * Functional interface for executing database operations within a managed JDBC transaction boundary.
 *
 * @param <T> Result type returned by transaction execution.
 */
@FunctionalInterface
public interface TransactionCallback<T> {
    T doInTransaction(Connection connection) throws Exception;
}
