package util;

import model.User;
import model.enums.UserRole;
import model.enums.UserStatus;
import service.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegistrationDatabaseTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART HIRE - REGISTRATION DATABASE VERIFICATION");
        System.out.println("==================================================");

        long timestamp = System.currentTimeMillis();
        String name = "Test User " + timestamp;
        String email = "test_" + timestamp + "@smarthire.com";
        String password = "Password123";

        try {
            UserService userService = new UserService();

            System.out.println("\n[STEP 1] Performing User Registration via UserService...");
            boolean registered = userService.registerUser(name, email, password, password, UserRole.JOB_SEEKER);
            if (!registered) {
                System.err.println("[FAIL] UserService.registerUser returned false.");
                return;
            }
            System.out.println("[PASS] UserService.registerUser returned TRUE");

            System.out.println("\n[STEP 2] Direct Querying MariaDB smarthire.users table...");
            int generatedId = 0;
            String userRole = null;
            String userStatus = null;

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, name, email, role, status FROM users WHERE email = ?")) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        generatedId = rs.getInt("id");
                        userRole = rs.getString("role");
                        userStatus = rs.getString("status");
                        System.out.println(" -> Verified Row in users Table:");
                        System.out.println("    ID: " + generatedId + " | Name: " + rs.getString("name") + " | Email: " + rs.getString("email") + " | Role: " + userRole + " | Status: " + userStatus);
                        System.out.println("[PASS] New user row EXISTS in MariaDB users table");
                    } else {
                        System.err.println("[FAIL] New user row NOT FOUND in MariaDB users table!");
                        return;
                    }
                }
            }

            System.out.println("\n[STEP 3] Direct Querying MariaDB smarthire.job_seekers profile table...");
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT user_id, phone, education, experience, location FROM job_seekers WHERE user_id = ?")) {
                stmt.setInt(1, generatedId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(" -> Verified Profile Row in job_seekers Table:");
                        System.out.println("    User ID: " + rs.getInt("user_id") + " | Phone: " + rs.getString("phone") + " | Location: " + rs.getString("location"));
                        System.out.println("[PASS] Corresponding job_seekers profile row EXISTS in MariaDB");
                    } else {
                        System.err.println("[FAIL] job_seekers profile row NOT FOUND for User ID: " + generatedId);
                        return;
                    }
                }
            }

            System.out.println("\n[STEP 4] Verifying Immediate Re-Login with SAME registered email & password...");
            User authenticatedUser = userService.loginUser(email, password);
            if (authenticatedUser != null && authenticatedUser.getId() == generatedId) {
                System.out.println(" -> Authenticated User ID: " + authenticatedUser.getId() + " | Email: " + authenticatedUser.getEmail());
                System.out.println("[PASS] Re-login with SAME credentials SUCCEEDED cleanly");
            } else {
                System.err.println("[FAIL] Re-login failed for newly registered email: " + email);
                return;
            }

            System.out.println("\n[STEP 5] Querying latest 5 users in MariaDB database...");
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, name, email, role, status FROM users ORDER BY id DESC LIMIT 5");
                 ResultSet rs = stmt.executeQuery()) {
                System.out.println("Latest users in MariaDB smarthire:");
                while (rs.next()) {
                    System.out.println(" -> ID: " + rs.getInt("id") + " | Name: " + rs.getString("name") + " | Email: " + rs.getString("email") + " | Role: " + rs.getString("role"));
                }
            }

            System.out.println("\n==================================================");
            System.out.println("   ALL REGISTRATION DATABASE TESTS PASSED CLEANLY!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("[FAIL] Registration Database Test Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
