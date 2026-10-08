package thread;

import model.enums.NotificationType;

import java.util.logging.Logger;

/**
 * Singleton Facade managing background execution, task submission, and metric tracking.
 */
public class BackgroundTaskManager {

    private static final Logger LOGGER = Logger.getLogger(BackgroundTaskManager.class.getName());

    private static SmartHireTaskExecutor taskExecutor;
    private static final ThreadSafeApplicationTracker applicationTracker = new ThreadSafeApplicationTracker();

    private BackgroundTaskManager() {}

    public static synchronized void init() {
        if (taskExecutor == null || taskExecutor.isShutdown()) {
            taskExecutor = new SmartHireTaskExecutor(5);
            LOGGER.info("BackgroundTaskManager initialized.");
        }
    }

    public static synchronized void shutdown() {
        if (taskExecutor != null) {
            taskExecutor.shutdown();
            LOGGER.info("BackgroundTaskManager shutdown completed.");
        }
    }

    public static void asyncNotification(int userId, String title, String message, NotificationType notificationType) {
        if (userId <= 0) return;
        ensureInitialized();
        NotificationTask task = new NotificationTask(userId, title, message, notificationType, applicationTracker);
        taskExecutor.submitTask(task);
    }

    public static void asyncApplicationEvent(int jobId, int userId, String title, String message, NotificationType notificationType) {
        ensureInitialized();
        if (jobId > 0) {
            applicationTracker.recordJobApplication(jobId);
        }
        if (userId > 0) {
            NotificationTask task = new NotificationTask(userId, title, message, notificationType, applicationTracker);
            taskExecutor.submitTask(task);
        }
    }

    public static ThreadSafeApplicationTracker getTracker() {
        return applicationTracker;
    }

    public static SmartHireTaskExecutor getExecutor() {
        ensureInitialized();
        return taskExecutor;
    }

    private static void ensureInitialized() {
        if (taskExecutor == null || taskExecutor.isShutdown()) {
            init();
        }
    }
}
