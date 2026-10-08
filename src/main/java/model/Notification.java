package model;

import model.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Notification entity for user alerts and updates.
 */
public class Notification {

    private int id;
    private int userId;
    private String title;
    private String message;
    private NotificationType notificationType;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification() {
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(int id, int userId, String title, String message,
                        NotificationType notificationType, boolean isRead) {
        this.id = id;
        this.userId = userId;
        setTitle(title);
        setMessage(message);
        setNotificationType(notificationType);
        this.isRead = isRead;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Notification title cannot be null.");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = Objects.requireNonNull(message, "Notification message cannot be null.");
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = Objects.requireNonNull(notificationType, "Notification type cannot be null.");
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsUnread() {
        this.isRead = false;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", notificationType=" + notificationType +
                ", isRead=" + isRead +
                '}';
    }
}
