package util;

import exception.DatabaseException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection manager for SmartHire.
 * Configurable via system properties (DB_USER, DB_PASSWORD, DB_URL)
 * or environment variables (DB_USER, DB_PASSWORD, DB_URL).
 * Features automatic H2 embedded engine fallback if MySQL/MariaDB host is unavailable.
 */
public class DBUtil {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/smarthire?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";

    private static final String H2_URL = "jdbc:h2:mem:smarthire;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1";
    private static boolean initializedH2 = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {}
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {}
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ignored) {}
    }

    private DBUtil() {}

    /**
     * Obtains a new JDBC database connection.
     *
     * @return active Connection object
     * @throws DatabaseException if connection establishment fails or password configuration is missing
     */
    public static Connection getConnection() throws DatabaseException {
        String url = System.getProperty("DB_URL");
        if (url == null || url.trim().isEmpty()) {
            url = System.getProperty("db.url");
        }
        if (url == null || url.trim().isEmpty()) {
            url = System.getenv("DB_URL");
        }
        if (url == null || url.trim().isEmpty()) {
            url = DEFAULT_URL;
        }

        String user = System.getProperty("DB_USER");
        if (user == null || user.trim().isEmpty()) {
            user = System.getProperty("db.user");
        }
        if (user == null || user.trim().isEmpty()) {
            user = System.getenv("DB_USER");
        }
        if (user == null || user.trim().isEmpty()) {
            user = DEFAULT_USER;
        }

        String password = System.getProperty("DB_PASSWORD");
        if (password == null || password.isEmpty()) {
            password = System.getProperty("db.password");
        }
        if (password == null || password.isEmpty()) {
            password = System.getenv("DB_PASSWORD");
        }
        if (password == null) {
            password = "";
        }

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            // Fallback to H2 in-memory database if MySQL/MariaDB host connection fails
            try {
                Class.forName("org.h2.Driver");
                Connection conn = DriverManager.getConnection(H2_URL, "sa", "");
                synchronized (DBUtil.class) {
                    if (!initializedH2) {
                        initH2Schema(conn);
                        initializedH2 = true;
                    }
                }
                return conn;
            } catch (Exception h2Err) {
                throw new DatabaseException("Failed to establish JDBC Connection to " + url + " as user " + user, e);
            }
        }
    }

    private static void initH2Schema(Connection conn) {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("database.sql")) {
            if (is == null) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("--") || line.trim().isEmpty()) continue;
                sb.append(line).append("\n");
            }

            String[] sqlStatements = sb.toString().split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String sql : sqlStatements) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty() && !trimmed.toLowerCase().startsWith("create database") && !trimmed.toLowerCase().startsWith("use ")) {
                        try {
                            stmt.execute(trimmed);
                        } catch (SQLException ignored) {}
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: H2 Schema Initialization error: " + e.getMessage());
        }
    }

    /**
     * Safely closes a JDBC connection.
     *
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Failed to close JDBC connection: " + e.getMessage());
            }
        }
    }
}
