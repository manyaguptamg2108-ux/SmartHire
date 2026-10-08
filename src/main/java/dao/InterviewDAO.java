package dao;

import exception.DatabaseException;
import model.Interview;
import model.enums.InterviewStatus;
import model.enums.InterviewType;
import util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class InterviewDAO implements InterviewDAOInterface {

    @Override
    public Interview findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM interviews WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapInterview(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Interview by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Interview> findByApplicationId(int applicationId) throws DatabaseException {
        List<Interview> interviews = new ArrayList<>();
        String sql = "SELECT * FROM interviews WHERE application_id = ? ORDER BY interview_date ASC, interview_time ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interviews.add(mapInterview(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Interviews for application ID: " + applicationId, e);
        }
        return interviews;
    }

    @Override
    public List<Interview> findAll() throws DatabaseException {
        List<Interview> interviews = new ArrayList<>();
        String sql = "SELECT * FROM interviews ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                interviews.add(mapInterview(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all Interviews", e);
        }
        return interviews;
    }

    @Override
    public boolean save(Interview interview) throws DatabaseException {
        String sql = "INSERT INTO interviews (application_id, interview_date, interview_time, interview_type, meeting_link, location, notes, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, interview.getApplicationId());
            stmt.setDate(2, Date.valueOf(interview.getInterviewDate()));
            stmt.setTime(3, Time.valueOf(interview.getInterviewTime()));
            stmt.setString(4, interview.getInterviewType().name());
            stmt.setString(5, interview.getMeetingLink());
            stmt.setString(6, interview.getLocation());
            stmt.setString(7, interview.getNotes());
            stmt.setString(8, interview.getStatus() != null ? interview.getStatus().name() : InterviewStatus.SCHEDULED.name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        interview.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving Interview for application ID " + interview.getApplicationId(), e);
        }
        return false;
    }

    @Override
    public boolean update(Interview interview) throws DatabaseException {
        String sql = "UPDATE interviews SET application_id = ?, interview_date = ?, interview_time = ?, interview_type = ?, meeting_link = ?, location = ?, notes = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, interview.getApplicationId());
            stmt.setDate(2, Date.valueOf(interview.getInterviewDate()));
            stmt.setTime(3, Time.valueOf(interview.getInterviewTime()));
            stmt.setString(4, interview.getInterviewType().name());
            stmt.setString(5, interview.getMeetingLink());
            stmt.setString(6, interview.getLocation());
            stmt.setString(7, interview.getNotes());
            stmt.setString(8, interview.getStatus().name());
            stmt.setInt(9, interview.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating Interview ID: " + interview.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM interviews WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting Interview ID: " + id, e);
        }
    }

    @Override
    public boolean updateInterviewStatus(int interviewId, InterviewStatus status) throws DatabaseException {
        String sql = "UPDATE interviews SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, interviewId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating status for Interview ID: " + interviewId, e);
        }
    }

    private Interview mapInterview(ResultSet rs) throws SQLException {
        Interview interview = new Interview();
        interview.setId(rs.getInt("id"));
        interview.setApplicationId(rs.getInt("application_id"));
        Date idate = rs.getDate("interview_date");
        if (idate != null) {
            interview.setInterviewDate(idate.toLocalDate());
        }
        Time itime = rs.getTime("interview_time");
        if (itime != null) {
            interview.setInterviewTime(itime.toLocalTime());
        }
        interview.setInterviewType(InterviewType.valueOf(rs.getString("interview_type")));
        interview.setMeetingLink(rs.getString("meeting_link"));
        interview.setLocation(rs.getString("location"));
        interview.setNotes(rs.getString("notes"));
        interview.setStatus(InterviewStatus.valueOf(rs.getString("status")));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            interview.setCreatedAt(created.toLocalDateTime());
        }
        return interview;
    }
}
