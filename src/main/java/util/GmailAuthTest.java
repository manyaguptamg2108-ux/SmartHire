package util;

import exception.AuthenticationException;
import model.User;
import model.enums.UserRole;
import service.UserService;

/**
 * Automated verification test verifying end-to-end Gmail account registration and subsequent login operations.
 */
public class GmailAuthTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART HIRE - GMAIL ACCOUNT REGISTRATION & LOGIN TEST");
        System.out.println("==================================================");

        boolean allPassed = true;
        UserService userService = new UserService();

        long timestamp = System.currentTimeMillis();
        String gmailEmail = "smart.hire.user." + timestamp + "@gmail.com";
        String password = "MySecurePassword123";

        try {
            // [1] Register new user with Gmail address
            System.out.print("[TEST 1] Register user with Gmail address: ");
            boolean registered = userService.registerUser("Gmail Test User", gmailEmail, password, password, UserRole.JOB_SEEKER);
            if (registered) {
                System.out.println("PASSED (" + gmailEmail + ")");
            } else {
                System.out.println("FAILED (registerUser returned false)");
                allPassed = false;
            }

            // [2] Immediate login with exact Gmail address
            System.out.print("[TEST 2] Login with exact Gmail address: ");
            User user1 = userService.loginUser(gmailEmail, password);
            if (user1 != null && user1.getEmail().equalsIgnoreCase(gmailEmail)) {
                System.out.println("PASSED (User ID: " + user1.getId() + ")");
            } else {
                System.out.println("FAILED");
                allPassed = false;
            }

            // [3] Login with uppercase Gmail address
            System.out.print("[TEST 3] Login with uppercase Gmail address: ");
            User user2 = userService.loginUser(gmailEmail.toUpperCase(), password);
            if (user2 != null && user2.getId() == user1.getId()) {
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED (Case sensitivity issue)");
                allPassed = false;
            }

            // [4] Login with leading/trailing spaces in Gmail address
            System.out.print("[TEST 4] Login with padded whitespace Gmail address: ");
            User user3 = userService.loginUser("  " + gmailEmail + "  ", password);
            if (user3 != null && user3.getId() == user1.getId()) {
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED (Whitespace issue)");
                allPassed = false;
            }

            // [5] Login with padded whitespace in password
            System.out.print("[TEST 5] Login with padded password whitespace: ");
            User user4 = userService.loginUser(gmailEmail, "  " + password + "  ");
            if (user4 != null && user4.getId() == user1.getId()) {
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED (Password whitespace issue)");
                allPassed = false;
            }

        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            e.printStackTrace();
            allPassed = false;
        }

        System.out.println("==================================================");
        if (allPassed) {
            System.out.println("   ALL GMAIL AUTHENTICATION TESTS PASSED CLEANLY!  ");
            System.out.println("==================================================");
        } else {
            System.out.println("   SOME TESTS FAILED - CHECK LOGS ABOVE           ");
            System.out.println("==================================================");
            System.exit(1);
        }
    }
}
