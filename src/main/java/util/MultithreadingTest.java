package util;

import model.enums.NotificationType;
import thread.BackgroundTaskManager;
import thread.SmartHireTaskExecutor;
import thread.ThreadSafeApplicationTracker;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Automated Test Suite for Step 8: Multithreading, ExecutorService, and Synchronization verification.
 */
public class MultithreadingTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART HIRE - STEP 8 MULTITHREADING VERIFICATION");
        System.out.println("==================================================");

        boolean allPassed = true;

        try {
            // Initialize BackgroundTaskManager & Executor
            BackgroundTaskManager.init();
            SmartHireTaskExecutor executor = BackgroundTaskManager.getExecutor();
            ThreadSafeApplicationTracker tracker = BackgroundTaskManager.getTracker();
            tracker.reset();

            // [1] Test ExecutorService Initialization
            System.out.println("\n[1] Testing ExecutorService Initialization...");
            if (executor != null && !executor.isShutdown()) {
                System.out.println(" -> ExecutorService initialized with fixed thread pool. PASSED.");
            } else {
                System.out.println(" -> ExecutorService initialization failed. FAILED.");
                allPassed = false;
            }

            // [2] Test Concurrent Task Execution & Worker Thread Detection
            System.out.println("\n[2] Testing Concurrent Task Execution & Worker Threads Detection...");
            Set<String> workerThreadsDetected = Collections.synchronizedSet(new HashSet<>());
            int taskCount = 20;
            CountDownLatch taskLatch = new CountDownLatch(taskCount);

            for (int i = 0; i < taskCount; i++) {
                executor.submitTask(() -> {
                    workerThreadsDetected.add(Thread.currentThread().getName());
                    tracker.incrementProcessedTasks();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ignored) {}
                    taskLatch.countDown();
                });
            }

            boolean tasksFinished = taskLatch.await(5, TimeUnit.SECONDS);
            System.out.println(" -> Tasks completed: " + tasksFinished + " (" + tracker.getTotalTasksProcessed() + "/" + taskCount + ")");
            System.out.println(" -> Worker threads detected: " + workerThreadsDetected);

            if (tasksFinished && tracker.getTotalTasksProcessed() == taskCount && workerThreadsDetected.size() > 1) {
                System.out.println("PASSED.");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [3] Test Synchronized Counter Accuracy (Mutual Exclusion)
            System.out.println("\n[3] Testing Synchronized Counter Accuracy (No Lost Updates)...");
            int numThreads = 10;
            int incrementsPerThread = 100;
            int expectedCount = numThreads * incrementsPerThread;
            CountDownLatch syncLatch = new CountDownLatch(numThreads);
            ExecutorService testPool = Executors.newFixedThreadPool(numThreads);

            for (int t = 0; t < numThreads; t++) {
                testPool.submit(() -> {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        tracker.incrementSynchronizedCounter();
                    }
                    syncLatch.countDown();
                });
            }

            syncLatch.await(5, TimeUnit.SECONDS);
            testPool.shutdown();
            int actualCount = tracker.getSynchronizedCounter();
            System.out.println(" -> Expected Count: " + expectedCount + " | Actual Count: " + actualCount);

            if (actualCount == expectedCount) {
                System.out.println("PASSED.");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [4] Test ConcurrentHashMap & AtomicInteger Consistency
            System.out.println("\n[4] Testing ConcurrentHashMap & AtomicInteger Consistency...");
            int concurrentWorkers = 10;
            int jobsCount = 5;
            CountDownLatch mapLatch = new CountDownLatch(concurrentWorkers);
            ExecutorService mapPool = Executors.newFixedThreadPool(concurrentWorkers);

            for (int w = 0; w < concurrentWorkers; w++) {
                mapPool.submit(() -> {
                    for (int jobId = 1; jobId <= jobsCount; jobId++) {
                        tracker.recordJobApplication(jobId);
                    }
                    mapLatch.countDown();
                });
            }

            mapLatch.await(5, TimeUnit.SECONDS);
            mapPool.shutdown();

            boolean mapConsistent = true;
            for (int jobId = 1; jobId <= jobsCount; jobId++) {
                int count = tracker.getJobApplicationCount(jobId);
                System.out.println(" -> Job #" + jobId + " recorded applications: " + count + " (Expected: " + concurrentWorkers + ")");
                if (count != concurrentWorkers) {
                    mapConsistent = false;
                }
            }

            if (mapConsistent) {
                System.out.println("PASSED.");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [5] Test Asynchronous Notification Persistence
            System.out.println("\n[5] Testing Asynchronous Notification Tasks Persistence...");
            int userForNotification = 1; // System Administrator User ID from database seed
            int asyncNotifCount = 5;
            int notifsBefore = tracker.getTotalNotificationsSent();

            for (int k = 1; k <= asyncNotifCount; k++) {
                BackgroundTaskManager.asyncNotification(
                    userForNotification, 
                    "Async Test Notification #" + k, 
                    "Test background message content " + System.currentTimeMillis(), 
                    NotificationType.GENERAL
                );
            }

            // Give background worker pool 2 seconds to process DB saves
            Thread.sleep(2000);
            int notifsAfter = tracker.getTotalNotificationsSent();
            System.out.println(" -> Notifications submitted: " + asyncNotifCount + " | Sent: " + (notifsAfter - notifsBefore));

            if ((notifsAfter - notifsBefore) == asyncNotifCount) {
                System.out.println("PASSED.");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [6] Test Graceful Executor Shutdown
            System.out.println("\n[6] Testing Graceful Executor Shutdown...");
            BackgroundTaskManager.shutdown();
            if (executor.isShutdown()) {
                System.out.println(" -> SmartHireTaskExecutor shut down cleanly. PASSED.");
            } else {
                System.out.println(" -> Executor shutdown failed. FAILED.");
                allPassed = false;
            }

            System.out.println("\n==================================================");
            if (allPassed) {
                System.out.println("   ALL STEP 8 MULTITHREADING TESTS PASSED CLEANLY!");
            } else {
                System.out.println("   SOME STEP 8 TESTS FAILED. CHECK LOGS ABOVE.");
                System.exit(1);
            }
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("MULTITHREADING TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
