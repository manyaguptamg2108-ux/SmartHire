package thread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe tracker maintaining background task metrics, per-job counters,
 * and synchronized state demonstration for thread safety.
 */
public class ThreadSafeApplicationTracker {

    private final AtomicInteger totalTasksProcessed = new AtomicInteger(0);
    private final AtomicInteger totalNotificationsSent = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, AtomicInteger> jobApplicationCounts = new ConcurrentHashMap<>();
    
    // Primitive counter used to demonstrate 'synchronized' mutual exclusion
    private int synchronizedCounter = 0;

    /**
     * Synchronized method enforcing mutual exclusion when incrementing counter.
     */
    public synchronized void incrementSynchronizedCounter() {
        this.synchronizedCounter++;
    }

    /**
     * Synchronized method retrieving current counter value.
     */
    public synchronized int getSynchronizedCounter() {
        return this.synchronizedCounter;
    }

    /**
     * Thread-safe recording of job application count using ConcurrentHashMap and AtomicInteger.
     */
    public void recordJobApplication(int jobId) {
        jobApplicationCounts.computeIfAbsent(jobId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * Retrieves application count for a specific job ID safely.
     */
    public int getJobApplicationCount(int jobId) {
        AtomicInteger count = jobApplicationCounts.get(jobId);
        return (count != null) ? count.get() : 0;
    }

    public void incrementProcessedTasks() {
        totalTasksProcessed.incrementAndGet();
    }

    public int getTotalTasksProcessed() {
        return totalTasksProcessed.get();
    }

    public void incrementNotificationsSent() {
        totalNotificationsSent.incrementAndGet();
    }

    public int getTotalNotificationsSent() {
        return totalNotificationsSent.get();
    }

    public ConcurrentHashMap<Integer, AtomicInteger> getJobApplicationCounts() {
        return jobApplicationCounts;
    }

    /**
     * Resets metrics to zero (useful for test isolation).
     */
    public synchronized void reset() {
        totalTasksProcessed.set(0);
        totalNotificationsSent.set(0);
        synchronizedCounter = 0;
        jobApplicationCounts.clear();
    }
}
