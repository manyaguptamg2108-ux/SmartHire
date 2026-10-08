package dao;

import exception.DatabaseException;
import model.Admin;
import model.JobSeeker;
import model.Recruiter;
import model.User;
import model.enums.UserRole;
import model.enums.UserStatus;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object implementation for User entity using PreparedStatement and try-with-resources.
 */
public class UserDAO implements UserDAOInterface {

    @Override
    public User findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by ID: " + id, e);
        }
        return null;
    }

    @Override
    public User findByEmail(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM users WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by email: " + email, e);
        }
        return null;
    }

    @Override
    public List<User> findAll() throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all users", e);
        }
        return users;
    }

    @Override
    public boolean save(User user) throws DatabaseException {
        String sql = "INSERT INTO users (name, email, password_hash, role, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getStatus() != null ? user.getStatus().name() : UserStatus.ACTIVE.name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving user: " + user.getEmail(), e);
        }
        return false;
    }

    @Override
    public boolean update(User user) throws DatabaseException {
        String sql = "UPDATE users SET name = ?, email = ?, password_hash = ?, role = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getStatus().name());
            stmt.setInt(6, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user ID: " + user.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user ID: " + id, e);
        }
    }

    @Override
    public boolean updateStatus(int userId, UserStatus status) throws DatabaseException {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating status for user ID: " + userId, e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        UserRole role = UserRole.valueOf(rs.getString("role"));
        UserStatus status = UserStatus.valueOf(rs.getString("status"));

        try {
            switch (role) {
                case JOB_SEEKER:
                    JobSeeker seeker = new JobSeeker();
                    seeker.setId(id);
                    seeker.setName(name);
                    seeker.setEmail(email);
                    seeker.setPasswordHash(passwordHash);
                    seeker.setStatus(status);
                    return seeker;
                case RECRUITER:
                    Recruiter recruiter = new Recruiter();
                    recruiter.setId(id);
                    recruiter.setName(name);
                    recruiter.setEmail(email);
                    recruiter.setPasswordHash(passwordHash);
                    recruiter.setStatus(status);
                    return recruiter;
                case ADMIN:
                default:
                    Admin admin = new Admin();
                    admin.setId(id);
                    admin.setName(name);
                    admin.setEmail(email);
                    admin.setPasswordHash(passwordHash);
                    admin.setStatus(status);
                    return admin;
            }
        } catch (Exception e) {
            throw new SQLException("Failed to map User object", e);
        }
    }
}
