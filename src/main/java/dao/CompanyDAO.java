package dao;

import exception.DatabaseException;
import model.Company;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO implements CompanyDAOInterface {

    @Override
    public Company findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM companies WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCompany(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Company by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Company> findByRecruiterId(int recruiterId) throws DatabaseException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies WHERE recruiter_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recruiterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    companies.add(mapCompany(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Companies by recruiter ID: " + recruiterId, e);
        }
        return companies;
    }

    @Override
    public List<Company> findAll() throws DatabaseException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                companies.add(mapCompany(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all Companies", e);
        }
        return companies;
    }

    @Override
    public boolean save(Company company) throws DatabaseException {
        String sql = "INSERT INTO companies (recruiter_id, company_name, description, location, website, industry, company_size) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, company.getRecruiterId());
            stmt.setString(2, company.getCompanyName());
            stmt.setString(3, company.getDescription());
            stmt.setString(4, company.getLocation());
            stmt.setString(5, company.getWebsite());
            stmt.setString(6, company.getIndustry());
            stmt.setString(7, company.getCompanySize());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        company.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving Company: " + company.getCompanyName(), e);
        }
        return false;
    }

    @Override
    public boolean update(Company company) throws DatabaseException {
        String sql = "UPDATE companies SET recruiter_id = ?, company_name = ?, description = ?, location = ?, website = ?, industry = ?, company_size = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, company.getRecruiterId());
            stmt.setString(2, company.getCompanyName());
            stmt.setString(3, company.getDescription());
            stmt.setString(4, company.getLocation());
            stmt.setString(5, company.getWebsite());
            stmt.setString(6, company.getIndustry());
            stmt.setString(7, company.getCompanySize());
            stmt.setInt(8, company.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating Company ID: " + company.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM companies WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting Company ID: " + id, e);
        }
    }

    private Company mapCompany(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setId(rs.getInt("id"));
        company.setRecruiterId(rs.getInt("recruiter_id"));
        try {
            company.setCompanyName(rs.getString("company_name"));
        } catch (Exception ignored) {}
        company.setDescription(rs.getString("description"));
        company.setLocation(rs.getString("location"));
        company.setWebsite(rs.getString("website"));
        company.setIndustry(rs.getString("industry"));
        company.setCompanySize(rs.getString("company_size"));
        return company;
    }
}
