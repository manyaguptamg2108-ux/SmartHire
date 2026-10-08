package model;

import exception.InvalidJobException;
import model.enums.JobStatus;
import model.enums.JobType;
import java.time.LocalDate;

/**
 * Job model class with strict domain validation rules.
 */
public class Job {

    private int id;
    private int companyId;
    private String title;
    private String description;
    private String location;
    private double salaryMin;
    private double salaryMax;
    private JobType jobType;
    private String experienceRequired;
    private String educationRequired;
    private LocalDate applicationDeadline;
    private JobStatus status;

    public Job() {
        this.status = JobStatus.ACTIVE;
    }

    public Job(int id, int companyId, String title, String description, String location,
               double salaryMin, double salaryMax, JobType jobType, String experienceRequired,
               String educationRequired, LocalDate applicationDeadline, JobStatus status) throws InvalidJobException {
        this.id = id;
        this.companyId = companyId;
        setTitle(title);
        setDescription(description);
        this.location = location;
        setSalaries(salaryMin, salaryMax);
        this.jobType = (jobType != null) ? jobType : JobType.FULL_TIME;
        this.experienceRequired = experienceRequired;
        this.educationRequired = educationRequired;
        setApplicationDeadline(applicationDeadline);
        this.status = (status != null) ? status : JobStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws InvalidJobException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidJobException("Job title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws InvalidJobException {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidJobException("Job description cannot be empty.");
        }
        this.description = description.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getSalaryMin() {
        return salaryMin;
    }

    public double getSalaryMax() {
        return salaryMax;
    }

    public void setSalaries(double salaryMin, double salaryMax) throws InvalidJobException {
        if (salaryMin < 0) {
            throw new InvalidJobException("Minimum salary cannot be negative.");
        }
        if (salaryMax < 0) {
            throw new InvalidJobException("Maximum salary cannot be negative.");
        }
        if (salaryMax < salaryMin) {
            throw new InvalidJobException("Maximum salary (" + salaryMax + ") cannot be less than minimum salary (" + salaryMin + ").");
        }
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getExperienceRequired() {
        return experienceRequired;
    }

    public void setExperienceRequired(String experienceRequired) {
        this.experienceRequired = experienceRequired;
    }

    public String getEducationRequired() {
        return educationRequired;
    }

    public void setEducationRequired(String educationRequired) {
        this.educationRequired = educationRequired;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) throws InvalidJobException {
        if (applicationDeadline == null) {
            throw new InvalidJobException("Application deadline is required.");
        }
        this.applicationDeadline = applicationDeadline;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", salaryRange=" + salaryMin + "-" + salaryMax +
                ", jobType=" + jobType +
                ", status=" + status +
                '}';
    }
}
