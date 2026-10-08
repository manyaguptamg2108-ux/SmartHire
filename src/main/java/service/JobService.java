package service;

import dao.*;
import exception.DatabaseException;
import exception.InvalidJobException;
import model.Company;
import model.Job;
import model.Skill;
import model.enums.JobStatus;
import model.enums.JobType;

import java.time.LocalDate;
import java.util.List;

/**
 * Service managing Job posting lifecycle, validation rules, and skill mappings.
 */
public class JobService {

    private final JobDAOInterface jobDAO;
    private final CompanyDAOInterface companyDAO;
    private final JobSkillsDAOInterface jobSkillsDAO;

    public JobService() {
        this.jobDAO = new JobDAO();
        this.companyDAO = new CompanyDAO();
        this.jobSkillsDAO = new JobSkillsDAO();
    }

    public JobService(JobDAOInterface jobDAO, CompanyDAOInterface companyDAO, JobSkillsDAOInterface jobSkillsDAO) {
        this.jobDAO = jobDAO;
        this.companyDAO = companyDAO;
        this.jobSkillsDAO = jobSkillsDAO;
    }

    public boolean createJob(Job job, List<Skill> requiredSkills) throws InvalidJobException, DatabaseException {
        validateJob(job, true);

        if (job.getStatus() == null) {
            job.setStatus(JobStatus.ACTIVE);
        }

        boolean saved = jobDAO.save(job);
        if (saved && requiredSkills != null && !requiredSkills.isEmpty()) {
            for (Skill skill : requiredSkills) {
                if (skill != null && skill.getId() > 0) {
                    jobSkillsDAO.addSkillToJob(job.getId(), skill.getId());
                }
            }
        }
        return saved;
    }

    public Job getJobById(int id) throws DatabaseException {
        return jobDAO.findById(id);
    }

    public List<Job> getAllJobs() throws DatabaseException {
        return jobDAO.findAll();
    }

    public List<Job> getJobsByCompany(int companyId) throws DatabaseException {
        return jobDAO.findByCompanyId(companyId);
    }

    public List<Job> searchJobs(String keyword, String location, JobType jobType, JobStatus status) throws DatabaseException {
        return jobDAO.searchJobs(keyword, location, jobType, status);
    }

    public boolean updateJob(Job job, List<Skill> requiredSkills) throws InvalidJobException, DatabaseException {
        if (job == null || job.getId() <= 0) {
            throw new InvalidJobException("Invalid Job for update.");
        }
        validateJob(job, false);

        boolean updated = jobDAO.update(job);
        if (updated && requiredSkills != null) {
            jobSkillsDAO.removeAllSkillsFromJob(job.getId());
            for (Skill skill : requiredSkills) {
                if (skill != null && skill.getId() > 0) {
                    jobSkillsDAO.addSkillToJob(job.getId(), skill.getId());
                }
            }
        }
        return updated;
    }

    public boolean deleteJob(int id) throws DatabaseException {
        jobSkillsDAO.removeAllSkillsFromJob(id);
        return jobDAO.delete(id);
    }

    public boolean closeJob(int jobId) throws DatabaseException {
        return jobDAO.updateJobStatus(jobId, JobStatus.CLOSED);
    }

    public boolean expireJob(int jobId) throws DatabaseException {
        return jobDAO.updateJobStatus(jobId, JobStatus.EXPIRED);
    }

    public boolean isJobExpired(Job job) {
        if (job == null) return true;
        if (job.getStatus() == JobStatus.EXPIRED || job.getStatus() == JobStatus.CLOSED) {
            return true;
        }
        return job.getApplicationDeadline() != null && job.getApplicationDeadline().isBefore(LocalDate.now());
    }

    public List<Skill> getJobSkills(int jobId) throws DatabaseException {
        return jobSkillsDAO.getSkillsForJob(jobId);
    }

    private void validateJob(Job job, boolean isNewCreation) throws InvalidJobException, DatabaseException {
        if (job == null) {
            throw new InvalidJobException("Job object cannot be null.");
        }
        if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
            throw new InvalidJobException("Job title is required.");
        }
        if (job.getDescription() == null || job.getDescription().trim().isEmpty()) {
            throw new InvalidJobException("Job description is required.");
        }
        if (job.getSalaryMin() < 0) {
            throw new InvalidJobException("Minimum salary cannot be negative.");
        }
        if (job.getSalaryMax() < 0) {
            throw new InvalidJobException("Maximum salary cannot be negative.");
        }
        if (job.getSalaryMax() < job.getSalaryMin()) {
            throw new InvalidJobException("Maximum salary cannot be less than minimum salary.");
        }
        Company company = companyDAO.findById(job.getCompanyId());
        if (company == null) {
            throw new InvalidJobException("Associated Company does not exist for ID: " + job.getCompanyId());
        }
        if (job.getApplicationDeadline() == null) {
            throw new InvalidJobException("Application deadline is required.");
        }
        if (isNewCreation && job.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new InvalidJobException("Application deadline cannot be in the past for a new job.");
        }
    }
}
