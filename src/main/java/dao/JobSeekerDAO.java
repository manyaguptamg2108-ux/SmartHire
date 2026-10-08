package dao;

import exception.DatabaseException;
import model.JobSeeker;
import model.enums.UserStatus;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobSeekerDAO implements JobSeekerDAOInterface {

    @Override
    public JobSeeker findById(int id) throws DatabaseException {
        String sql = "SELECT js.*, u.name, u.email, u.password_hash, u.status " +
                     "FROM job_seekers js JOIN users u ON js.user_id = u.id WHERE js.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapJobSeeker(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding JobSeeker by ID: " + id, e);
        }
        return null;
    }

    @Override
    public JobSeeker findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT js.*, u.name, u.email, u.password_hash, u.status " +
                     "FROM job_seekers js JOIN users u ON js.user_id = u.id WHERE js.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapJobSeeker(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding JobSeeker by User ID: " + userId, e);
        }
        return null;
    }

    @Override
    public List<JobSeeker> findAll() throws DatabaseException {
        List<JobSeeker> seekers = new ArrayList<>();
        String sql = "SELECT js.*, u.name, u.email, u.password_hash, u.status " +
                     "FROM job_seekers js JOIN users u ON js.user_id = u.id ORDER BY js.id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                seekers.add(mapJobSeeker(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all JobSeekers", e);
        }
        return seekers;
    }

    @Override
    public boolean save(JobSeeker seeker) throws DatabaseException {
        String sql = "INSERT INTO job_seekers (user_id, phone, education, experience, location, bio, resume_path, profile_completion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, seeker.getId());
            stmt.setString(2, seeker.getPhone());
            stmt.setString(3, seeker.getEducation());
            stmt.setString(4, seeker.getExperience());
            stmt.setString(5, seeker.getLocation());
            stmt.setString(6, seeker.getBio());
            stmt.setString(7, seeker.getResumePath());
            stmt.setInt(8, seeker.getProfileCompletion());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // JobSeeker record id generated
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving JobSeeker profile", e);
        }
        return false;
    }

    @Override
    public boolean update(JobSeeker seeker) throws DatabaseException {
        String sql = "UPDATE job_seekers SET phone = ?, education = ?, experience = ?, location = ?, bio = ?, resume_path = ?, profile_completion = ? WHERE id = ? OR user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seeker.getPhone());
            stmt.setString(2, seeker.getEducation());
            stmt.setString(3, seeker.getExperience());
            stmt.setString(4, seeker.getLocation());
            stmt.setString(5, seeker.getBio());
            stmt.setString(6, seeker.getResumePath());
            stmt.setInt(7, seeker.getProfileCompletion());
            stmt.setInt(8, seeker.getId());
            stmt.setInt(9, seeker.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating JobSeeker profile", e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM job_seekers WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting JobSeeker profile ID: " + id, e);
        }
    }

    private JobSeeker mapJobSeeker(ResultSet rs) throws SQLException {
        JobSeeker seeker = new JobSeeker();
        seeker.setId(rs.getInt("user_id"));
        try {
            seeker.setName(rs.getString("name"));
            seeker.setEmail(rs.getString("email"));
            seeker.setPasswordHash(rs.getString("password_hash"));
        } catch (Exception e) {
            throw new SQLException("Failed to map user attributes in JobSeeker", e);
        }
        seeker.setStatus(UserStatus.valueOf(rs.getString("status")));
        seeker.setPhone(rs.getString("phone"));
        seeker.setEducation(rs.getString("education"));
        seeker.setExperience(rs.getString("experience"));
        seeker.setLocation(rs.getString("location"));
        seeker.setBio(rs.getString("bio"));
        seeker.setResumePath(rs.getString("resume_path"));
        try {
            seeker.setProfileCompletion(rs.getInt("profile_completion"));
        } catch (Exception ignored) {}
        return seeker;
    }
}
