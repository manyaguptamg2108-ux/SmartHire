package service;

import dao.NotificationDAO;
import dao.NotificationDAOInterface;
import dao.UserDAO;
import dao.UserDAOInterface;
import exception.DatabaseException;
import exception.InvalidUserException;
import model.Notification;
import model.User;
import model.enums.NotificationType;

import java.util.List;

/**
 * Service managing user in-app notifications synchronously.
 */
public class NotificationService {

    private final NotificationDAOInterface notificationDAO;
    private final UserDAOInterface userDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.userDAO = new UserDAO();
    }

    public NotificationService(NotificationDAOInterface notificationDAO, UserDAOInterface userDAO) {
        this.notificationDAO = notificationDAO;
        this.userDAO = userDAO;
    }

    public boolean createNotification(int userId, String title, String message, NotificationType notificationType)
            throws InvalidUserException, DatabaseException {

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new InvalidUserException("Target user does not exist for ID: " + userId);
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidUserException("Notification title is required.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidUserException("Notification message is required.");
        }
        if (notificationType == null) {
            notificationType = NotificationType.GENERAL;
        }

        Notification notification = new Notification(0, userId, title, message, notificationType, false);
        return notificationDAO.save(notification);
    }

    public List<Notification> getUserNotifications(int userId) throws DatabaseException {
        return notificationDAO.findByUserId(userId);
    }

    public List<Notification> getUnreadNotifications(int userId) throws DatabaseException {
        return notificationDAO.findUnreadByUserId(userId);
    }

    public boolean markAsRead(int notificationId) throws DatabaseException {
        return notificationDAO.markAsRead(notificationId);
    }

    public boolean markAllAsRead(int userId) throws DatabaseException {
        return notificationDAO.markAllAsRead(userId);
    }

    public boolean deleteNotification(int id) throws DatabaseException {
        return notificationDAO.delete(id);
    }
}
