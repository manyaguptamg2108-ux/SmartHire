package util;

import dao.GenericDAO;
import dao.UserDAO;
import exception.AuthenticationException;
import exception.DatabaseException;
import exception.TransactionException;
import service.ApplicationTransactionService;
import service.UserService;
import thread.BackgroundTaskManager;
import thread.SmartHireTaskExecutor;

/**
 * Automated Code Quality & Architectural Smoke Test Suite for Step 10.
 * Verifies key architectural layers, class structures, package organization, and security utilities.
 */
public class CodeQualitySmokeTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART HIRE - STEP 10 CODE QUALITY SMOKE TEST   ");
        System.out.println("==================================================");

        boolean allPassed = true;

        try {
            // [1] Verify Package Structure & Layered Architecture
            System.out.println("\n[1] Verifying Layered Package Architecture...");
            Class<?> userClass = Class.forName("model.User");
            Class<?> userDAOClass = Class.forName("dao.UserDAO");
            Class<?> userServiceClass = Class.forName("service.UserService");
            Class<?> authFilterClass = Class.forName("controller.AuthenticationFilter");

            System.out.println(" -> Model package: " + userClass.getName());
            System.out.println(" -> DAO package: " + userDAOClass.getName());
            System.out.println(" -> Service package: " + userServiceClass.getName());
            System.out.println(" -> Controller package: " + authFilterClass.getName());
            System.out.println("PASSED.");

            // [2] Verify GenericDAO Contract & Type Abstraction
            System.out.println("\n[2] Verifying GenericDAO<T> Contract...");
            Class<?> genericDAOClass = Class.forName("dao.GenericDAO");
            if (genericDAOClass.isInterface() && GenericDAO.class.isAssignableFrom(UserDAO.class)) {
                System.out.println(" -> GenericDAO<T> interface verified. PASSED.");
            } else {
                System.out.println(" -> GenericDAO verification failed. FAILED.");
                allPassed = false;
            }

            // [3] Verify Custom Exceptions Hierarchy
            System.out.println("\n[3] Verifying Custom Exceptions Hierarchy...");
            if (Exception.class.isAssignableFrom(AuthenticationException.class) &&
                Exception.class.isAssignableFrom(DatabaseException.class) &&
                Exception.class.isAssignableFrom(TransactionException.class)) {
                System.out.println(" -> Custom exception hierarchy verified (AuthenticationException, DatabaseException, TransactionException). PASSED.");
            } else {
                System.out.println(" -> Custom exceptions verification failed. FAILED.");
                allPassed = false;
            }

            // [4] Verify Threading & Async Architecture
            System.out.println("\n[4] Verifying Multithreading Architecture Classes...");
            Class<?> executorClass = SmartHireTaskExecutor.class;
            Class<?> managerClass = BackgroundTaskManager.class;
            Class<?> listenerClass = Class.forName("thread.SmartHireAppListener");

            System.out.println(" -> Task Executor: " + executorClass.getName());
            System.out.println(" -> Background Task Manager: " + managerClass.getName());
            System.out.println(" -> App Listener: " + listenerClass.getName());
            System.out.println("PASSED.");

            // [5] Verify JDBC Transaction Management Architecture
            System.out.println("\n[5] Verifying Transaction Management Architecture...");
            Class<?> txManagerClass = TransactionManager.class;
            Class<?> txServiceClass = ApplicationTransactionService.class;

            System.out.println(" -> Transaction Manager: " + txManagerClass.getName());
            System.out.println(" -> Transaction Service: " + txServiceClass.getName());
            System.out.println("PASSED.");

            // [6] Verify Security & Password Utilities
            System.out.println("\n[6] Verifying Security & SHA-256 Hashing Utilities...");
            String rawPassword = "TestPassword123";
            String hash1 = PasswordUtil.hashPassword(rawPassword);
            boolean verified = PasswordUtil.verifyPassword(rawPassword, hash1);

            System.out.println(" -> SHA-256 Hash computed: " + hash1.substring(0, 16) + "...");
            System.out.println(" -> SHA-256 Verification: " + verified);

            if (verified && hash1.length() == 64) {
                System.out.println("PASSED.");
            } else {
                System.out.println(" -> Password security utility failed. FAILED.");
                allPassed = false;
            }

            System.out.println("\n==================================================");
            if (allPassed) {
                System.out.println("   ALL STEP 10 CODE QUALITY CHECKS PASSED CLEANLY!");
            } else {
                System.out.println("   SOME CODE QUALITY CHECKS FAILED.");
                System.exit(1);
            }
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("CODE QUALITY SMOKE TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
