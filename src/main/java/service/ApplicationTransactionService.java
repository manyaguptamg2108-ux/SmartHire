package service;

import dao.*;
import exception.DatabaseException;
import exception.InvalidApplicationException;
import exception.TransactionException;
import model.Application;
import model.Job;
import model.JobSeeker;
import model.Notification;
import model.enums.ApplicationStatus;
import model.enums.JobStatus;
import model.enums.NotificationType;
import util.TransactionManager;

import java.time.LocalDate;

/**
 * Service providing atomic multi-step transactional database operations for job application submission
 * and notification creation.
 */
public class ApplicationTransactionService {

    private final ApplicationDAOInterface applicationDAO;
    private final NotificationDAOInterface notificationDAO;
    private final JobDAOInterface jobDAO;
    private final JobSeekerDAOInterface jobSeekerDAO;

    public ApplicationTransactionService() {
        this.applicationDAO = new ApplicationDAO();
        this.notificationDAO = new NotificationDAO();
        this.jobDAO = new JobDAO();
        this.jobSeekerDAO = new JobSeekerDAO();
    }

    public ApplicationTransactionService(ApplicationDAOInterface applicationDAO, NotificationDAOInterface notificationDAO,
                                         JobDAOInterface jobDAO, JobSeekerDAOInterface jobSeekerDAO) {
        this.applicationDAO = applicationDAO;
        this.notificationDAO = notificationDAO;
        this.jobDAO = jobDAO;
        this.jobSeekerDAO = jobSeekerDAO;
    }

    /**
     * Executes atomic application submission and DB notification creation in a SINGLE JDBC Transaction.
     * If both succeed -> COMMIT. If notification fails -> ROLLBACK (Application is not saved).
     */
    public Application submitApplicationAtomic(int jobId, int jobSeekerId, String coverLetter, double matchPercentage, int notificationUserId)
            throws InvalidApplicationException, DatabaseException, TransactionException {

        // Pre-transaction validation checks
        Job job = jobDAO.findById(jobId);
        if (job == null) {
            throw new InvalidApplicationException("Target job does not exist for ID: " + jobId);
        }
        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new InvalidApplicationException("Cannot apply to job. Current job status is: " + job.getStatus());
        }
        if (job.getApplicationDeadline() != null && job.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new InvalidApplicationException("Cannot apply. Application deadline passed on: " + job.getApplicationDeadline());
        }

        JobSeeker seeker = jobSeekerDAO.findById(jobSeekerId);
        if (seeker == null) {
            seeker = jobSeekerDAO.findByUserId(jobSeekerId);
            if (seeker == null) {
                throw new InvalidApplicationException("Job Seeker profile does not exist for ID: " + jobSeekerId);
            }
            jobSeekerId = seeker.getId();
        }

        final int targetSeekerId = jobSeekerId;

        // Execute atomic multi-step operations inside JDBC Transaction boundary
        return TransactionManager.executeInTransaction(connection -> {
            // Step 1: Save Application using transaction Connection
            Application app = new Application(0, jobId, targetSeekerId, ApplicationStatus.APPLIED, matchPercentage, coverLetter);
            boolean appSaved = applicationDAO.save(connection, app);
            if (!appSaved) {
                throw new DatabaseException("Failed to insert Application row inside transaction.");
            }

            // Step 2: Save Notification using the SAME transaction Connection
            Notification notification = new Notification(
                0, 
                notificationUserId, 
                "Application Submitted", 
                "Your application for Job #" + jobId + " (" + job.getTitle() + ") was received successfully.", 
                NotificationType.NEW_APPLICATION, 
                false
            );
            boolean notifSaved = notificationDAO.save(connection, notification);
            if (!notifSaved) {
                throw new DatabaseException("Failed to insert Notification row inside transaction.");
            }

            return app;
        });
    }

    /**
     * Forces a transaction failure by attempting to insert a notification with an invalid User ID (-999).
     * This violates the foreign key constraint 'fk_notifications_users', causing a ROLLBACK.
     */
    public Application submitApplicationForcedFailure(int jobId, int jobSeekerId, String coverLetter, double matchPercentage, int invalidUserId)
            throws TransactionException {

        return TransactionManager.executeInTransaction(connection -> {
            // Step 1: Save valid Application row inside transaction Connection
            Application app = new Application(0, jobId, jobSeekerId, ApplicationStatus.APPLIED, matchPercentage, coverLetter);
            boolean appSaved = applicationDAO.save(connection, app);
            if (!appSaved) {
                throw new DatabaseException("Failed to insert Application row.");
            }

            // Step 2: Intentionally attempt inserting Notification with invalid user_id = -999 (causes Foreign Key Violation Exception)
            Notification invalidNotif = new Notification(
                0, 
                invalidUserId, // Foreign Key constraint violation
                "Forced Failure Test", 
                "This will trigger a foreign key violation and force ROLLBACK.", 
                NotificationType.NEW_APPLICATION, 
                false
            );
            notificationDAO.save(connection, invalidNotif);

            return app;
        });
    }
}
