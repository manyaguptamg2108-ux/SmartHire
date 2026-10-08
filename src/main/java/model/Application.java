package model;

import exception.InvalidApplicationException;
import model.enums.ApplicationStatus;
import java.time.LocalDateTime;

/**
 * Application model class linking Job Seekers to posted Jobs.
 */
public class Application {

    private int id;
    private int jobId;
    private int jobSeekerId;
    private ApplicationStatus status;
    private double matchPercentage;
    private String coverLetter;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    public Application() {
        this.status = ApplicationStatus.APPLIED;
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Application(int id, int jobId, int jobSeekerId, ApplicationStatus status,
                       double matchPercentage, String coverLetter) throws InvalidApplicationException {
        this.id = id;
        this.jobId = jobId;
        this.jobSeekerId = jobSeekerId;
        this.status = (status != null) ? status : ApplicationStatus.APPLIED;
        setMatchPercentage(matchPercentage);
        this.coverLetter = coverLetter;
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getJobSeekerId() {
        return jobSeekerId;
    }

    public void setJobSeekerId(int jobSeekerId) {
        this.jobSeekerId = jobSeekerId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(ApplicationStatus newStatus) {
        setStatus(newStatus);
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) throws InvalidApplicationException {
        if (matchPercentage < 0 || matchPercentage > 100) {
            throw new InvalidApplicationException("Match percentage must be between 0 and 100. Provided: " + matchPercentage);
        }
        this.matchPercentage = matchPercentage;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", jobSeekerId=" + jobSeekerId +
                ", status=" + status +
                ", matchPercentage=" + matchPercentage + "%" +
                ", appliedAt=" + appliedAt +
                '}';
    }
}
