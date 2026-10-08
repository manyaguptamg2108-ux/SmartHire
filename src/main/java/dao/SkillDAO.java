package dao;

import exception.DatabaseException;
import model.Skill;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO implements SkillDAOInterface {

    @Override
    public Skill findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM skills WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSkill(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Skill by ID: " + id, e);
        }
        return null;
    }

    @Override
    public Skill findByName(String skillName) throws DatabaseException {
        String sql = "SELECT * FROM skills WHERE LOWER(skill_name) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, skillName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSkill(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Skill by name: " + skillName, e);
        }
        return null;
    }

    @Override
    public List<Skill> findAll() throws DatabaseException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT * FROM skills ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                skills.add(mapSkill(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all Skills", e);
        }
        return skills;
    }

    @Override
    public boolean save(Skill skill) throws DatabaseException {
        String sql = "INSERT INTO skills (skill_name) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, skill.getSkillName());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        skill.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving Skill: " + skill.getSkillName(), e);
        }
        return false;
    }

    @Override
    public boolean update(Skill skill) throws DatabaseException {
        String sql = "UPDATE skills SET skill_name = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, skill.getSkillName());
            stmt.setInt(2, skill.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating Skill ID: " + skill.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM skills WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting Skill ID: " + id, e);
        }
    }

    private Skill mapSkill(ResultSet rs) throws SQLException {
        Skill skill = new Skill();
        skill.setId(rs.getInt("id"));
        skill.setSkillName(rs.getString("skill_name"));
        return skill;
    }
}
