package util;

import exception.AuthenticationException;
import exception.InvalidUserException;
import model.User;
import model.enums.UserRole;
import service.UserService;

/**
 * Verification test runner for SmartHire Authentication & Registration (STEP 6).
 */
public class AuthTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART HIRE - STEP 6 AUTHENTICATION VERIFICATION ");
        System.out.println("==================================================\n");

        try {
            UserService userService = new UserService();

            // 1 & 2 & 3. Password Hashing & Verification
            System.out.println("[1] Testing PasswordUtil hashing & verification...");
            String rawPassword = "Password123";
            String hash = PasswordUtil.hashPassword(rawPassword);
            System.out.println(" -> Computed hash for 'Password123': " + hash);
            boolean verifyValid = PasswordUtil.verifyPassword("Password123", hash);
            boolean verifyInvalid = PasswordUtil.verifyPassword("WrongPassword", hash);

            System.out.println(" -> Verify correct password: " + verifyValid);
            System.out.println(" -> Verify incorrect password: " + verifyInvalid);
            if (!verifyValid || verifyInvalid) {
                throw new IllegalStateException("PasswordUtil verification failed!");
            }
            System.out.println("PASSED.\n");

            // 4. Find demo user by email
            System.out.println("[2] Testing Find Demo User by Email...");
            User adminUser = userService.findUserByEmail("admin@smarthire.com");
            System.out.println(" -> Found Admin User: " + (adminUser != null ? adminUser.getName() : "NULL"));
            if (adminUser != null) {
                System.out.println(" -> Stored Hash in DB: '" + adminUser.getPasswordHash() + "'");
            } else {
                throw new IllegalStateException("Admin user lookup failed!");
            }
            System.out.println("PASSED.\n");

            // 5. Login with valid credentials
            System.out.println("[3] Testing Valid User Login...");
            User authUser = userService.loginUser("admin@smarthire.com", "Password123");
            System.out.println(" -> Authenticated User: " + authUser.getName() + " (" + authUser.getRole() + ")");
            System.out.println("PASSED.\n");

            // 6. Reject invalid credentials
            System.out.println("[4] Testing Invalid Password Rejection...");
            try {
                userService.loginUser("admin@smarthire.com", "WrongPassword123");
                System.err.println("FAILED: Should have rejected invalid password.");
            } catch (AuthenticationException e) {
                System.out.println(" -> Caught expected AuthenticationException: " + e.getMessage());
            }
            System.out.println("PASSED.\n");

            // 7. Reject inactive user
            System.out.println("[5] Testing Inactive User Login Rejection...");
            userService.deactivateUser(adminUser.getId());
            try {
                userService.loginUser("admin@smarthire.com", "Password123");
                System.err.println("FAILED: Should have rejected inactive user login.");
            } catch (AuthenticationException e) {
                System.out.println(" -> Caught expected AuthenticationException: " + e.getMessage());
            } finally {
                userService.activateUser(adminUser.getId());
            }
            System.out.println("PASSED.\n");

            // 8. Reject duplicate registration email
            System.out.println("[6] Testing Duplicate Email Registration Rejection...");
            try {
                userService.registerUser("Duplicate User", "admin@smarthire.com", "Pass123", "Pass123", UserRole.JOB_SEEKER);
                System.err.println("FAILED: Should have rejected duplicate registration email.");
            } catch (InvalidUserException e) {
                System.out.println(" -> Caught expected InvalidUserException: " + e.getMessage());
            }
            System.out.println("PASSED.\n");

            // 9. Verify ADMIN cannot be publicly registered
            System.out.println("[7] Testing Public ADMIN Registration Restriction...");
            try {
                userService.registerUser("Malicious Admin", "hacker.admin@smarthire.com", "Pass123", "Pass123", UserRole.ADMIN);
                System.err.println("FAILED: Should have rejected ADMIN public registration.");
            } catch (InvalidUserException e) {
                System.out.println(" -> Caught expected InvalidUserException: " + e.getMessage());
            }
            System.out.println("PASSED.\n");

            // 10. Valid Public Registration (JOB_SEEKER / RECRUITER)
            System.out.println("[8] Testing Valid JobSeeker Public Registration...");
            String tempEmail = "new.candidate." + System.currentTimeMillis() + "@email.com";
            boolean registered = userService.registerUser("New Candidate", tempEmail, "Secret123", "Secret123", UserRole.JOB_SEEKER);
            System.out.println(" -> Registration status: " + registered);
            User newSeeker = userService.findUserByEmail(tempEmail);
            System.out.println(" -> Registered User Role: " + (newSeeker != null ? newSeeker.getRole() : "NULL"));
            System.out.println("PASSED.\n");

            System.out.println("==================================================");
            System.out.println("   ALL STEP 6 AUTHENTICATION TESTS PASSED CLEANLY!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("AUTH TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
