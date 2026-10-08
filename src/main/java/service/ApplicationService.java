package service;

import dao.*;
import exception.DatabaseException;
import exception.InvalidApplicationException;
import model.Application;
import model.Job;
import model.JobSeeker;
import model.enums.ApplicationStatus;
import model.enums.JobStatus;
import model.enums.NotificationType;
import thread.BackgroundTaskManager;

import java.time.LocalDate;
import java.util.List;

/**
 * Service managing Job Applications, eligibility validation, workflow status transitions,
 * and asynchronous background notification dispatch.
 */
public class ApplicationService {

    private final ApplicationDAOInterface applicationDAO;
    private final JobDAOInterface jobDAO;
    private final JobSeekerDAOInterface jobSeekerDAO;

    public ApplicationService() {
        this.applicationDAO = new ApplicationDAO();
        this.jobDAO = new JobDAO();
        this.jobSeekerDAO = new JobSeekerDAO();
    }

    public ApplicationService(ApplicationDAOInterface applicationDAO, JobDAOInterface jobDAO, JobSeekerDAOInterface jobSeekerDAO) {
        this.applicationDAO = applicationDAO;
        this.jobDAO = jobDAO;
        this.jobSeekerDAO = jobSeekerDAO;
    }

    public boolean applyForJob(int jobId, int jobSeekerId, String coverLetter, double matchPercentage)
            throws InvalidApplicationException, DatabaseException {

        // 1. Job exists check
        Job job = jobDAO.findById(jobId);
        if (job == null) {
            throw new InvalidApplicationException("Target job does not exist for ID: " + jobId);
        }

        // 2. Job status ACTIVE check
        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new InvalidApplicationException("Cannot apply to job. Current job status is: " + job.getStatus());
        }

        // 3. Application deadline check
        if (job.getApplicationDeadline() != null && job.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new InvalidApplicationException("Cannot apply. Application deadline passed on: " + job.getApplicationDeadline());
        }

        // 4. Job seeker exists check
        JobSeeker seeker = jobSeekerDAO.findById(jobSeekerId);
        if (seeker == null) {
            seeker = jobSeekerDAO.findByUserId(jobSeekerId);
            if (seeker == null) {
                throw new InvalidApplicationException("Job Seeker profile does not exist for ID: " + jobSeekerId);
            }
            jobSeekerId = seeker.getId();
        }

        // 5. Duplicate application check
        List<Application> existingApps = applicationDAO.findByJobSeekerId(jobSeekerId);
        for (Application app : existingApps) {
            if (app.getJobId() == jobId) {
                throw new InvalidApplicationException("Job Seeker has already submitted an application for this job.");
            }
        }

        // 6. Match percentage range validation
        if (matchPercentage < 0 || matchPercentage > 100) {
            throw new InvalidApplicationException("Match percentage must be between 0 and 100.");
        }

        Application application = new Application(0, jobId, jobSeekerId, ApplicationStatus.APPLIED, matchPercentage, coverLetter);
        boolean saved = applicationDAO.save(application);

        if (saved) {
            // Submit asynchronous background task for notification & tracker metrics
            int targetUserId = (seeker != null) ? seeker.getId() : jobSeekerId;
            BackgroundTaskManager.asyncApplicationEvent(
                jobId, 
                targetUserId, 
                "Application Submitted", 
                "Your application for Job #" + jobId + " (" + job.getTitle() + ") was received successfully.", 
                NotificationType.NEW_APPLICATION
            );
        }

        return saved;
    }

    public Application getApplicationById(int id) throws DatabaseException {
        return applicationDAO.findById(id);
    }

    public List<Application> getApplicationsByJob(int jobId) throws DatabaseException {
        return applicationDAO.findByJobId(jobId);
    }

    public List<Application> getApplicationsByJobSeeker(int jobSeekerId) throws DatabaseException {
        return applicationDAO.findByJobSeekerId(jobSeekerId);
    }

    public List<Application> getAllApplications() throws DatabaseException {
        return applicationDAO.findAll();
    }

    /**
     * Updates application status after enforcing workflow transition rules.
     */
    public boolean updateApplicationStatus(int applicationId, ApplicationStatus newStatus)
            throws InvalidApplicationException, DatabaseException {

        Application app = applicationDAO.findById(applicationId);
        if (app == null) {
            throw new InvalidApplicationException("Application not found for ID: " + applicationId);
        }
        if (newStatus == null) {
            throw new InvalidApplicationException("New application status cannot be null.");
        }

        validateStatusTransition(app.getStatus(), newStatus);
        boolean updated = applicationDAO.updateApplicationStatus(applicationId, newStatus);

        if (updated) {
            // Submit asynchronous background task for status change notification
            BackgroundTaskManager.asyncNotification(
                app.getJobSeekerId(), 
                "Application Status Update", 
                "Your application #" + applicationId + " status has been updated to: " + newStatus, 
                NotificationType.APPLICATION_STATUS
            );
        }

        return updated;
    }

    public boolean updateMatchPercentage(int applicationId, double matchPercentage)
            throws InvalidApplicationException, DatabaseException {
        if (matchPercentage < 0 || matchPercentage > 100) {
            throw new InvalidApplicationException("Match percentage must be between 0 and 100.");
        }
        return applicationDAO.updateMatchPercentage(applicationId, matchPercentage);
    }

    public boolean withdrawApplication(int applicationId) throws InvalidApplicationException, DatabaseException {
        return updateApplicationStatus(applicationId, ApplicationStatus.WITHDRAWN);
    }

    /**
     * Validates status workflow transitions.
     */
    private void validateStatusTransition(ApplicationStatus current, ApplicationStatus target)
            throws InvalidApplicationException {
        if (current == target) return;

        if (current == ApplicationStatus.SELECTED || current == ApplicationStatus.REJECTED || current == ApplicationStatus.WITHDRAWN) {
            throw new InvalidApplicationException("Cannot change status from terminal state '" + current + "' to '" + target + "'.");
        }

        switch (current) {
            case APPLIED:
                if (target != ApplicationStatus.UNDER_REVIEW && target != ApplicationStatus.REJECTED && target != ApplicationStatus.WITHDRAWN) {
                    throw new InvalidApplicationException("Invalid transition from APPLIED to " + target);
                }
                break;
            case UNDER_REVIEW:
                if (target != ApplicationStatus.SHORTLISTED && target != ApplicationStatus.REJECTED && target != ApplicationStatus.WITHDRAWN) {
                    throw new InvalidApplicationException("Invalid transition from UNDER_REVIEW to " + target);
                }
                break;
            case SHORTLISTED:
                if (target != ApplicationStatus.INTERVIEW && target != ApplicationStatus.REJECTED && target != ApplicationStatus.WITHDRAWN) {
                    throw new InvalidApplicationException("Invalid transition from SHORTLISTED to " + target);
                }
                break;
            case INTERVIEW:
                if (target != ApplicationStatus.SELECTED && target != ApplicationStatus.REJECTED && target != ApplicationStatus.WITHDRAWN) {
                    throw new InvalidApplicationException("Invalid transition from INTERVIEW to " + target);
                }
                break;
            default:
                throw new InvalidApplicationException("Unsupported status transition from " + current + " to " + target);
        }
    }
}
