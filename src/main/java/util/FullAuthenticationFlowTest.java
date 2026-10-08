package util;

import exception.AuthenticationException;
import exception.InvalidUserException;
import model.User;
import model.enums.UserRole;
import model.enums.UserStatus;
import service.UserService;

/**
 * Complete End-to-End Regression Test Suite for Full Authentication & Registration Flow.
 * Verifies User Registration, Password Hashing, Session State, Logins with SAME Credentials,
 * Relogin after Logout, Duplicate Email Protection, and Account Inactivity Enforcement.
 */
public class FullAuthenticationFlowTest {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   SMART HIRE - FULL AUTHENTICATION & REGRESSION TEST SUITE       ");
        System.out.println("==================================================================");

        boolean allPassed = true;
        UserService userService = new UserService();

        long timestamp = System.currentTimeMillis();
        String testName = "FullAuth Candidate";
        String rawEmail = "FullAuth_" + timestamp + "@SmartHire.com";
        String normalizedEmail = rawEmail.trim().toLowerCase();
        String rawPassword = "SecurePass123!";

        try {
            // 1. Create & Register Unique User
            System.out.print("[PASS 1] Registration of new candidate: ");
            boolean registered = userService.registerUser(testName, rawEmail, rawPassword, rawPassword, UserRole.JOB_SEEKER);
            if (registered) {
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED (Registration returned false)");
                allPassed = false;
            }

            // 2. Verify Database Row & Normalized Email Lookup
            System.out.print("[PASS 2] Database row lookup by email: ");
            User dbUser = userService.findUserByEmail(rawEmail);
            if (dbUser != null) {
                System.out.println("PASSED (Found ID: " + dbUser.getId() + ")");
            } else {
                System.out.println("FAILED (User not found in DB)");
                allPassed = false;
            }

            // 3. Verify Password Hashing (SHA-256 64-char Hex)
            System.out.print("[PASS 3] Password hash verification in database: ");
            if (dbUser != null && dbUser.getPasswordHash() != null && dbUser.getPasswordHash().length() == 64) {
                String expectedHash = PasswordUtil.hashPassword(rawPassword);
                if (dbUser.getPasswordHash().equalsIgnoreCase(expectedHash)) {
                    System.out.println("PASSED (SHA-256 Hash: " + dbUser.getPasswordHash().substring(0, 16) + "...)");
                } else {
                    System.out.println("FAILED (Stored hash does not match computed hash)");
                    allPassed = false;
                }
            } else {
                System.out.println("FAILED (Password hash is missing or invalid length)");
                allPassed = false;
            }

            // 4. Verify Generated User ID > 0
            System.out.print("[PASS 4] Generated Database User ID validity: ");
            if (dbUser != null && dbUser.getId() > 0) {
                System.out.println("PASSED (Valid User ID: " + dbUser.getId() + ")");
            } else {
                System.out.println("FAILED (Invalid or missing User ID)");
                allPassed = false;
            }

            // 5. Initial Login with SAME Registered Email & Password
            System.out.print("[PASS 5] Initial Login with SAME credentials: ");
            User authenticatedUser = userService.loginUser(rawEmail, rawPassword);
            if (authenticatedUser != null && authenticatedUser.getId() == dbUser.getId()) {
                System.out.println("PASSED (Authenticated User ID: " + authenticatedUser.getId() + ")");
            } else {
                System.out.println("FAILED (Login failed with newly registered credentials)");
                allPassed = false;
            }

            // 6. Simulate Logout & Relogin with Mixed-Case Email & SAME Password
            System.out.print("[PASS 6] Relogin after Logout with mixed-case email: ");
            User reloggedUser = userService.loginUser("FULLAUTH_" + timestamp + "@SMARTHIRE.COM", rawPassword);
            if (reloggedUser != null && reloggedUser.getEmail().equalsIgnoreCase(normalizedEmail)) {
                System.out.println("PASSED (Relogin Successful, Role: " + reloggedUser.getRole() + ")");
            } else {
                System.out.println("FAILED (Relogin failed)");
                allPassed = false;
            }

            // 7. Duplicate Email Registration Rejection
            System.out.print("[PASS 7] Duplicate email registration rejection: ");
            try {
                userService.registerUser(testName, normalizedEmail, rawPassword, rawPassword, UserRole.JOB_SEEKER);
                System.out.println("FAILED (Duplicate email allowed)");
                allPassed = false;
            } catch (InvalidUserException e) {
                if (e.getMessage().contains("already registered")) {
                    System.out.println("PASSED (Rejected: " + e.getMessage() + ")");
                } else {
                    System.out.println("FAILED (" + e.getMessage() + ")");
                    allPassed = false;
                }
            }

            // 8. Wrong Password Rejection
            System.out.print("[PASS 8] Wrong password rejection: ");
            try {
                userService.loginUser(rawEmail, "WrongPassword999!");
                System.out.println("FAILED (Wrong password accepted)");
                allPassed = false;
            } catch (AuthenticationException e) {
                System.out.println("PASSED (Rejected with invalid credentials message)");
            }

            // 9. Unknown Email Rejection
            System.out.print("[PASS 9] Unknown email login rejection: ");
            try {
                userService.loginUser("unknown_user_9999@smarthire.com", rawPassword);
                System.out.println("FAILED (Unknown email accepted)");
                allPassed = false;
            } catch (AuthenticationException e) {
                System.out.println("PASSED (Rejected unknown email)");
            }

            // 10. Inactive Account Login Rejection
            System.out.print("[PASS 10] Inactive account login rejection: ");
            if (dbUser != null) {
                userService.deactivateUser(dbUser.getId());
                try {
                    userService.loginUser(rawEmail, rawPassword);
                    System.out.println("FAILED (Inactive account login succeeded)");
                    allPassed = false;
                } catch (AuthenticationException e) {
                    if (e.getMessage().contains("Account is inactive")) {
                        System.out.println("PASSED (Inactive account rejected)");
                    } else {
                        System.out.println("FAILED (" + e.getMessage() + ")");
                        allPassed = false;
                    }
                }
                // Restore active status
                userService.activateUser(dbUser.getId());
            }

        } catch (Exception e) {
            System.out.println("ERROR: Unexpected exception during test run: " + e.getMessage());
            e.printStackTrace();
            allPassed = false;
        }

        System.out.println("==================================================================");
        if (allPassed) {
            System.out.println("✅ RESULT: ALL FULL AUTHENTICATION FLOW TESTS PASSED!");
        } else {
            System.out.println("❌ RESULT: TEST SUITE FAILED - SEE DETAILED LOG ABOVE.");
        }
        System.out.println("==================================================================");
    }
}
