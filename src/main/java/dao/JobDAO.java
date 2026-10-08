package dao;

import exception.DatabaseException;
import model.Job;
import model.enums.JobStatus;
import model.enums.JobType;
import util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobDAO implements JobDAOInterface {

    @Override
    public Job findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM jobs WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapJob(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Job by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Job> findByCompanyId(int companyId) throws DatabaseException {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE company_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapJob(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding Jobs for company ID: " + companyId, e);
        }
        return jobs;
    }

    @Override
    public List<Job> findAll() throws DatabaseException {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                jobs.add(mapJob(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all Jobs", e);
        }
        return jobs;
    }

    @Override
    public boolean save(Job job) throws DatabaseException {
        String sql = "INSERT INTO jobs (company_id, title, description, location, salary_min, salary_max, job_type, experience_required, education_required, application_deadline, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, job.getCompanyId());
            stmt.setString(2, job.getTitle());
            stmt.setString(3, job.getDescription());
            stmt.setString(4, job.getLocation());
            stmt.setDouble(5, job.getSalaryMin());
            stmt.setDouble(6, job.getSalaryMax());
            stmt.setString(7, job.getJobType().name());
            stmt.setString(8, job.getExperienceRequired());
            stmt.setString(9, job.getEducationRequired());
            stmt.setDate(10, Date.valueOf(job.getApplicationDeadline()));
            stmt.setString(11, job.getStatus() != null ? job.getStatus().name() : JobStatus.ACTIVE.name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        job.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving Job: " + job.getTitle(), e);
        }
        return false;
    }

    @Override
    public boolean update(Job job) throws DatabaseException {
        String sql = "UPDATE jobs SET company_id = ?, title = ?, description = ?, location = ?, salary_min = ?, salary_max = ?, job_type = ?, experience_required = ?, education_required = ?, application_deadline = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, job.getCompanyId());
            stmt.setString(2, job.getTitle());
            stmt.setString(3, job.getDescription());
            stmt.setString(4, job.getLocation());
            stmt.setDouble(5, job.getSalaryMin());
            stmt.setDouble(6, job.getSalaryMax());
            stmt.setString(7, job.getJobType().name());
            stmt.setString(8, job.getExperienceRequired());
            stmt.setString(9, job.getEducationRequired());
            stmt.setDate(10, Date.valueOf(job.getApplicationDeadline()));
            stmt.setString(11, job.getStatus().name());
            stmt.setInt(12, job.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating Job ID: " + job.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM jobs WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting Job ID: " + id, e);
        }
    }

    @Override
    public boolean updateJobStatus(int jobId, JobStatus status) throws DatabaseException {
        String sql = "UPDATE jobs SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, jobId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating status for Job ID: " + jobId, e);
        }
    }

    @Override
    public List<Job> searchJobs(String keyword, String location, JobType jobType, JobStatus status) throws DatabaseException {
        List<Job> jobs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM jobs WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(title) LIKE ? OR LOWER(description) LIKE ?)");
            String k = "%" + keyword.trim().toLowerCase() + "%";
            params.add(k);
            params.add(k);
        }

        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND LOWER(location) LIKE ?");
            params.add("%" + location.trim().toLowerCase() + "%");
        }

        if (jobType != null) {
            sql.append(" AND job_type = ?");
            params.add(jobType.name());
        }

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }

        sql.append(" ORDER BY created_at DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapJob(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching jobs", e);
        }

        return jobs;
    }

    private Job mapJob(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setId(rs.getInt("id"));
        job.setCompanyId(rs.getInt("company_id"));
        try {
            job.setTitle(rs.getString("title"));
            job.setDescription(rs.getString("description"));
        } catch (Exception ignored) {}
        job.setLocation(rs.getString("location"));
        try {
            job.setSalaries(rs.getDouble("salary_min"), rs.getDouble("salary_max"));
        } catch (Exception ignored) {}
        job.setJobType(JobType.valueOf(rs.getString("job_type")));
        job.setExperienceRequired(rs.getString("experience_required"));
        job.setEducationRequired(rs.getString("education_required"));
        Date deadline = rs.getDate("application_deadline");
        if (deadline != null) {
            try {
                job.setApplicationDeadline(deadline.toLocalDate());
            } catch (Exception ignored) {}
        }
        job.setStatus(JobStatus.valueOf(rs.getString("status")));
        return job;
    }
}
