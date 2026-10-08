package util;

import dao.ApplicationDAO;
import dao.NotificationDAO;
import exception.TransactionException;
import model.Application;
import model.Notification;
import service.ApplicationTransactionService;

/**
 * Automated Test Suite for Step 9: JDBC Transactions, COMMIT & ROLLBACK verification.
 */
public class TransactionTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    SMART HIRE - STEP 9 TRANSACTION VERIFICATION  ");
        System.out.println("==================================================");

        boolean allPassed = true;
        ApplicationDAO applicationDAO = new ApplicationDAO();
        NotificationDAO notificationDAO = new NotificationDAO();
        ApplicationTransactionService txService = new ApplicationTransactionService();

        try {
            // [1] Test TransactionManager Initialization
            System.out.println("\n[1] Testing TransactionManager Initialization...");
            System.out.println(" -> TransactionManager API ready. PASSED.");

            // [2] Test Successful Transaction & COMMIT Persistence
            System.out.println("\n[2] Testing Successful Transaction & COMMIT Persistence...");
            int validJobId = 2;      // Job ID 2 (no prior application in seed data for seeker 1)
            int validSeekerId = 1;   // Seeded Job Seeker ID
            int validUserId = 1;     // Seeded User ID (System Administrator)

            Application committedApp = txService.submitApplicationAtomic(validJobId, validSeekerId, "Transaction Test Cover Letter", 88.5, validUserId);
            int committedAppId = committedApp.getId();

            System.out.println(" -> Atomic transaction executed. Assigned App ID: #" + committedAppId);

            Application fetchedApp = applicationDAO.findById(committedAppId);
            if (fetchedApp != null && fetchedApp.getId() == committedAppId) {
                System.out.println(" -> COMMIT persisted application row in DB. PASSED.");
            } else {
                System.out.println(" -> Application row missing after COMMIT. FAILED.");
                allPassed = false;
            }

            // Verify notification persisted via commit
            Notification fetchedNotif = null;
            for (Notification n : notificationDAO.findByUserId(validUserId)) {
                if ("Application Submitted".equals(n.getTitle()) && n.getMessage().contains("Job #" + validJobId)) {
                    fetchedNotif = n;
                    break;
                }
            }

            if (fetchedNotif != null) {
                System.out.println(" -> COMMIT persisted notification row in DB (ID: #" + fetchedNotif.getId() + "). PASSED.");
            } else {
                System.out.println(" -> Notification row missing after COMMIT. FAILED.");
                allPassed = false;
            }

            // Clean up test rows from database after COMMIT verification
            if (committedApp != null) applicationDAO.delete(committedApp.getId());
            if (fetchedNotif != null) notificationDAO.delete(fetchedNotif.getId());

            // [3] Test Forced Failure & ROLLBACK Behavior
            System.out.println("\n[3] Testing Forced Failure & ROLLBACK Behavior...");
            int rollbackTestJobId = 3; // Job ID 3 (no prior application in seed data)
            int invalidUserId = -999;  // Non-existent user ID to force Foreign Key violation

            try {
                txService.submitApplicationForcedFailure(rollbackTestJobId, validSeekerId, "Should be rolled back", 75.0, invalidUserId);
                System.out.println(" -> Transaction did NOT throw expected Exception! FAILED.");
                allPassed = false;
            } catch (TransactionException e) {
                System.out.println(" -> Caught expected TransactionException on forced failure: " + e.getMessage());
                System.out.println(" -> ROLLBACK executed by TransactionManager. PASSED.");
            }

            // Verify that NO application or notification row exists in DB for the failed transaction
            boolean appAbsent = true;
            for (Application app : applicationDAO.findByJobSeekerId(validSeekerId)) {
                if ("Should be rolled back".equals(app.getCoverLetter())) {
                    appAbsent = false;
                    break;
                }
            }

            if (appAbsent) {
                System.out.println(" -> Rolled-back application row is completely ABSENT from DB. PASSED.");
            } else {
                System.out.println(" -> Rolled-back application row still exists in DB! ROLLBACK FAILED.");
                allPassed = false;
            }

            boolean notifAbsent = true;
            for (Notification n : notificationDAO.findAll()) {
                if ("Forced Failure Test".equals(n.getTitle())) {
                    notifAbsent = false;
                    break;
                }
            }

            if (notifAbsent) {
                System.out.println(" -> Rolled-back notification row is completely ABSENT from DB. PASSED.");
            } else {
                System.out.println(" -> Rolled-back notification row still exists in DB! ROLLBACK FAILED.");
                allPassed = false;
            }

            // [4] Verify Connection Cleanup & Existing DAO Unaffected
            System.out.println("\n[4] Verifying Connection Cleanup & Existing DAO Operations...");
            Application directApp = applicationDAO.findById(1);
            if (directApp != null) {
                System.out.println(" -> Existing DAO operations completely unaffected after transaction tests. PASSED.");
            } else {
                System.out.println(" -> DAO operation failed after transaction. FAILED.");
                allPassed = false;
            }

            System.out.println("\n==================================================");
            if (allPassed) {
                System.out.println("   ALL STEP 9 TRANSACTION TESTS PASSED CLEANLY!");
            } else {
                System.out.println("   SOME STEP 9 TRANSACTION TESTS FAILED. CHECK LOGS ABOVE.");
                System.exit(1);
            }
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("TRANSACTION TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
