package dao;

import exception.DatabaseException;
import model.Notification;

import java.sql.Connection;
import java.util.List;

public interface NotificationDAOInterface extends GenericDAO<Notification> {
    List<Notification> findByUserId(int userId) throws DatabaseException;
    List<Notification> findUnreadByUserId(int userId) throws DatabaseException;
    boolean markAsRead(int notificationId) throws DatabaseException;
    boolean markAllAsRead(int userId) throws DatabaseException;
    
    // Transaction-aware save overload method using provided JDBC Connection
    boolean save(Connection conn, Notification notification) throws DatabaseException;
}
