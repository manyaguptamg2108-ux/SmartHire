package dao;

import exception.DatabaseException;
import model.Skill;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobSkillsDAO implements JobSkillsDAOInterface {

    @Override
    public boolean addSkillToJob(int jobId, int skillId) throws DatabaseException {
        String sql = "INSERT INTO job_skills (job_id, skill_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            stmt.setInt(2, skillId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Handle duplicate entry constraint safely if already mapped
            if (e.getErrorCode() == 1062) {
                return false;
            }
            throw new DatabaseException("Error linking skill ID " + skillId + " to job ID " + jobId, e);
        }
    }

    @Override
    public boolean removeSkillFromJob(int jobId, int skillId) throws DatabaseException {
        String sql = "DELETE FROM job_skills WHERE job_id = ? AND skill_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            stmt.setInt(2, skillId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing skill ID " + skillId + " from job ID " + jobId, e);
        }
    }

    @Override
    public List<Skill> getSkillsForJob(int jobId) throws DatabaseException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT s.* FROM skills s JOIN job_skills js ON s.id = js.skill_id WHERE js.job_id = ? ORDER BY s.skill_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Skill skill = new Skill();
                    skill.setId(rs.getInt("id"));
                    skill.setSkillName(rs.getString("skill_name"));
                    skills.add(skill);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving skills for job ID: " + jobId, e);
        }
        return skills;
    }

    @Override
    public boolean removeAllSkillsFromJob(int jobId) throws DatabaseException {
        String sql = "DELETE FROM job_skills WHERE job_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing all skills for job ID: " + jobId, e);
        }
    }
}
