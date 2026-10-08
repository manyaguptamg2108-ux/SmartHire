package thread;

import model.enums.NotificationType;
import service.NotificationService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Asynchronous background worker task for processing and persisting user notifications.
 */
public class NotificationTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(NotificationTask.class.getName());

    private final int userId;
    private final String title;
    private final String message;
    private final NotificationType notificationType;
    private final ThreadSafeApplicationTracker tracker;

    public NotificationTask(int userId, String title, String message, NotificationType notificationType, ThreadSafeApplicationTracker tracker) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.tracker = tracker;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        try {
            LOGGER.info(String.format("[ASYNC TASK] [%s] Processing Notification Task for User ID: %d | Title: '%s'", threadName, userId, title));

            // Independent service instance per thread execution
            NotificationService notificationService = new NotificationService();
            boolean success = notificationService.createNotification(userId, title, message, notificationType);

            if (success && tracker != null) {
                tracker.incrementNotificationsSent();
                tracker.incrementProcessedTasks();
            }

            LOGGER.info(String.format("[ASYNC TASK] [%s] Notification Task Completed for User ID: %d | Status: %s", threadName, userId, success ? "SUCCESS" : "FAILED"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("[ASYNC TASK] [%s] Exception executing Notification Task for User ID %d: %s", threadName, userId, e.getMessage()), e);
        }
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }
}
