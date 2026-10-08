package dao;

import exception.DatabaseException;
import model.Recruiter;
import model.enums.UserStatus;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecruiterDAO implements RecruiterDAOInterface {

    @Override
    public Recruiter findById(int id) throws DatabaseException {
        String sql = "SELECT r.*, u.name, u.email, u.password_hash, u.status " +
                     "FROM recruiters r JOIN users u ON r.user_id = u.id WHERE r.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRecruiter(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Recruiter by ID: " + id, e);
        }
        return null;
    }

    @Override
    public Recruiter findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT r.*, u.name, u.email, u.password_hash, u.status " +
                     "FROM recruiters r JOIN users u ON r.user_id = u.id WHERE r.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRecruiter(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Recruiter by User ID: " + userId, e);
        }
        return null;
    }

    @Override
    public List<Recruiter> findAll() throws DatabaseException {
        List<Recruiter> recruiters = new ArrayList<>();
        String sql = "SELECT r.*, u.name, u.email, u.password_hash, u.status " +
                     "FROM recruiters r JOIN users u ON r.user_id = u.id ORDER BY r.id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                recruiters.add(mapRecruiter(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all Recruiters", e);
        }
        return recruiters;
    }

    @Override
    public boolean save(Recruiter recruiter) throws DatabaseException {
        String sql = "INSERT INTO recruiters (user_id, phone, designation) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, recruiter.getId());
            stmt.setString(2, recruiter.getPhone());
            stmt.setString(3, recruiter.getDesignation());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error saving Recruiter profile", e);
        }
    }

    @Override
    public boolean update(Recruiter recruiter) throws DatabaseException {
        String sql = "UPDATE recruiters SET phone = ?, designation = ? WHERE id = ? OR user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recruiter.getPhone());
            stmt.setString(2, recruiter.getDesignation());
            stmt.setInt(3, recruiter.getId());
            stmt.setInt(4, recruiter.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating Recruiter profile", e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM recruiters WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting Recruiter profile ID: " + id, e);
        }
    }

    private Recruiter mapRecruiter(ResultSet rs) throws SQLException {
        Recruiter recruiter = new Recruiter();
        recruiter.setId(rs.getInt("user_id"));
        try {
            recruiter.setName(rs.getString("name"));
            recruiter.setEmail(rs.getString("email"));
            recruiter.setPasswordHash(rs.getString("password_hash"));
        } catch (Exception e) {
            throw new SQLException("Failed to map user attributes in Recruiter", e);
        }
        recruiter.setStatus(UserStatus.valueOf(rs.getString("status")));
        recruiter.setPhone(rs.getString("phone"));
        recruiter.setDesignation(rs.getString("designation"));
        return recruiter;
    }
}
