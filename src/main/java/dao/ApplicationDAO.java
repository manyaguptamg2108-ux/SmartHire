package dao;

import exception.DatabaseException;
import model.Application;
import model.enums.ApplicationStatus;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO implements ApplicationDAOInterface {

    @Override
    public Application findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM applications WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapApplication(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Application by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Application> findByJobId(int jobId) throws DatabaseException {
        List<Application> apps = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE job_id = ? ORDER BY applied_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apps.add(mapApplication(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Applications for job ID: " + jobId, e);
        }
        return apps;
    }

    @Override
    public List<Application> findByJobSeekerId(int jobSeekerId) throws DatabaseException {
        List<Application> apps = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE job_seeker_id = ? ORDER BY applied_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobSeekerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apps.add(mapApplication(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Applications for job seeker ID: " + jobSeekerId, e);
        }
        return apps;
    }

    @Override
    public List<Application> findAll() throws DatabaseException {
        List<Application> apps = new ArrayList<>();
        String sql = "SELECT * FROM applications ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                apps.add(mapApplication(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all Applications", e);
        }
        return apps;
    }

    @Override
    public boolean save(Application app) throws DatabaseException {
        try (Connection conn = DBUtil.getConnection()) {
            return save(conn, app);
        } catch (SQLException e) {
            throw new DatabaseException("Error establishing database connection for saving Application", e);
        }
    }

    @Override
    public boolean save(Connection conn, Application app) throws DatabaseException {
        String sql = "INSERT INTO applications (job_id, job_seeker_id, status, match_percentage, cover_letter) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, app.getJobId());
            stmt.setInt(2, app.getJobSeekerId());
            stmt.setString(3, app.getStatus() != null ? app.getStatus().name() : ApplicationStatus.APPLIED.name());
            stmt.setDouble(4, app.getMatchPercentage());
            stmt.setString(5, app.getCoverLetter());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        app.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving Application in transaction for job " + app.getJobId() + " by seeker " + app.getJobSeekerId(), e);
        }
        return false;
    }

    @Override
    public boolean update(Application app) throws DatabaseException {
        String sql = "UPDATE applications SET job_id = ?, job_seeker_id = ?, status = ?, match_percentage = ?, cover_letter = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, app.getJobId());
            stmt.setInt(2, app.getJobSeekerId());
            stmt.setString(3, app.getStatus().name());
            stmt.setDouble(4, app.getMatchPercentage());
            stmt.setString(5, app.getCoverLetter());
            stmt.setInt(6, app.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating Application ID: " + app.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM applications WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting Application ID: " + id, e);
        }
    }

    @Override
    public boolean updateApplicationStatus(int applicationId, ApplicationStatus status) throws DatabaseException {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, applicationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating status for Application ID: " + applicationId, e);
        }
    }

    @Override
    public boolean updateMatchPercentage(int applicationId, double matchPercentage) throws DatabaseException {
        String sql = "UPDATE applications SET match_percentage = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, matchPercentage);
            stmt.setInt(2, applicationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating match percentage for Application ID: " + applicationId, e);
        }
    }

    @Override
    public boolean withdrawApplication(int applicationId) throws DatabaseException {
        return updateApplicationStatus(applicationId, ApplicationStatus.WITHDRAWN);
    }

    private Application mapApplication(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setId(rs.getInt("id"));
        app.setJobId(rs.getInt("job_id"));
        app.setJobSeekerId(rs.getInt("job_seeker_id"));
        app.setStatus(ApplicationStatus.valueOf(rs.getString("status")));
        try {
            app.setMatchPercentage(rs.getDouble("match_percentage"));
        } catch (Exception ignored) {}
        app.setCoverLetter(rs.getString("cover_letter"));
        Timestamp applied = rs.getTimestamp("applied_at");
        if (applied != null) {
            app.setAppliedAt(applied.toLocalDateTime());
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            app.setUpdatedAt(updated.toLocalDateTime());
        }
        return app;
    }
}
