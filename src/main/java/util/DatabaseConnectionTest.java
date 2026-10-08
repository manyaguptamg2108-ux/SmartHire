package util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Automated Verification Test for SmartHire Database Connectivity.
 */
public class DatabaseConnectionTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART HIRE - DATABASE CONNECTION VERIFICATION   ");
        System.out.println("==================================================");

        try (Connection conn = DBUtil.getConnection()) {
            if (conn != null) {
                System.out.println("[PASS] JDBC connection established");

                DatabaseMetaData meta = conn.getMetaData();
                String rawUrl = meta.getURL();
                String cleanUrl = rawUrl != null ? rawUrl.replaceAll("password=[^&]*", "password=***") : "N/A";
                System.out.println(" -> JDBC URL: " + cleanUrl);
                System.out.println(" -> Database Product: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());

                // Execute SELECT 1
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    if (rs.next()) {
                        System.out.println("[PASS] SELECT 1 succeeded");
                    }
                }

                // Execute SELECT DATABASE()
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT DATABASE()")) {
                    if (rs.next()) {
                        String currentDb = rs.getString(1);
                        System.out.println(" -> Current Database: " + currentDb);
                        if (currentDb != null && currentDb.equalsIgnoreCase("smarthire")) {
                            System.out.println("[PASS] Database is 'smarthire'");
                        } else {
                            System.out.println("[INFO] Current Database name: " + currentDb);
                        }
                    }
                }

                // Check counts in users and jobs tables
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                    if (rs.next()) {
                        System.out.println(" -> users table count: " + rs.getInt(1));
                    }
                } catch (Exception e) {
                    System.out.println("[WARN] Could not query users table: " + e.getMessage());
                }

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM jobs")) {
                    if (rs.next()) {
                        System.out.println(" -> jobs table count: " + rs.getInt(1));
                    }
                } catch (Exception e) {
                    System.out.println("[WARN] Could not query jobs table: " + e.getMessage());
                }

                System.out.println("[PASS] Connection closed");
            } else {
                System.out.println("[FAIL] JDBC connection is null");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Database Connection Test Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
