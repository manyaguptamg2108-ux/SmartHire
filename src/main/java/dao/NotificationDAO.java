package dao;

import exception.DatabaseException;
import model.Notification;
import model.enums.NotificationType;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO implements NotificationDAOInterface {

    @Override
    public Notification findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapNotification(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Notification by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Notification> findByUserId(int userId) throws DatabaseException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapNotification(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Notifications for user ID: " + userId, e);
        }
        return notifications;
    }

    @Override
    public List<Notification> findUnreadByUserId(int userId) throws DatabaseException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapNotification(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding unread Notifications for user ID: " + userId, e);
        }
        return notifications;
    }

    @Override
    public List<Notification> findAll() throws DatabaseException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                notifications.add(mapNotification(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all Notifications", e);
        }
        return notifications;
    }

    @Override
    public boolean save(Notification notification) throws DatabaseException {
        try (Connection conn = DBUtil.getConnection()) {
            return save(conn, notification);
        } catch (SQLException e) {
            throw new DatabaseException("Error establishing database connection for saving Notification", e);
        }
    }

    @Override
    public boolean save(Connection conn, Notification notification) throws DatabaseException {
        String sql = "INSERT INTO notifications (user_id, title, message, notification_type, is_read) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getNotificationType().name());
            stmt.setBoolean(5, notification.isRead());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        notification.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving Notification in transaction for user ID " + notification.getUserId(), e);
        }
        return false;
    }

    @Override
    public boolean update(Notification notification) throws DatabaseException {
        String sql = "UPDATE applications SET user_id = ?, title = ?, message = ?, notification_type = ?, is_read = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getNotificationType().name());
            stmt.setBoolean(5, notification.isRead());
            stmt.setInt(6, notification.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating Notification ID: " + notification.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting Notification ID: " + id, e);
        }
    }

    @Override
    public boolean markAsRead(int notificationId) throws DatabaseException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error marking Notification ID " + notificationId + " as read", e);
        }
    }

    @Override
    public boolean markAllAsRead(int userId) throws DatabaseException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error marking all notifications for user ID " + userId + " as read", e);
        }
    }

    private Notification mapNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setNotificationType(NotificationType.valueOf(rs.getString("notification_type")));
        notification.setRead(rs.getBoolean("is_read"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            notification.setCreatedAt(created.toLocalDateTime());
        }
        return notification;
    }
}
