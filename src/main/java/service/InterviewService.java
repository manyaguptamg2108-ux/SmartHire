package service;

import dao.ApplicationDAO;
import dao.ApplicationDAOInterface;
import dao.InterviewDAO;
import dao.InterviewDAOInterface;
import exception.DatabaseException;
import exception.InvalidApplicationException;
import model.Application;
import model.Interview;
import model.enums.ApplicationStatus;
import model.enums.InterviewStatus;
import model.enums.InterviewType;

import java.util.List;

/**
 * Service managing Candidate Interview scheduling and status tracking.
 */
public class InterviewService {

    private final InterviewDAOInterface interviewDAO;
    private final ApplicationDAOInterface applicationDAO;

    public InterviewService() {
        this.interviewDAO = new InterviewDAO();
        this.applicationDAO = new ApplicationDAO();
    }

    public InterviewService(InterviewDAOInterface interviewDAO, ApplicationDAOInterface applicationDAO) {
        this.interviewDAO = interviewDAO;
        this.applicationDAO = applicationDAO;
    }

    public boolean scheduleInterview(Interview interview) throws InvalidApplicationException, DatabaseException {
        validateInterview(interview);

        if (interview.getStatus() == null) {
            interview.setStatus(InterviewStatus.SCHEDULED);
        }

        boolean saved = interviewDAO.save(interview);
        if (saved) {
            // Update application status to INTERVIEW automatically
            Application app = applicationDAO.findById(interview.getApplicationId());
            if (app != null && app.getStatus() != ApplicationStatus.INTERVIEW) {
                applicationDAO.updateApplicationStatus(app.getId(), ApplicationStatus.INTERVIEW);
            }
        }
        return saved;
    }

    public Interview getInterviewById(int id) throws DatabaseException {
        return interviewDAO.findById(id);
    }

    public List<Interview> getInterviewsByApplication(int applicationId) throws DatabaseException {
        return interviewDAO.findByApplicationId(applicationId);
    }

    public List<Interview> getAllInterviews() throws DatabaseException {
        return interviewDAO.findAll();
    }

    public boolean updateInterview(Interview interview) throws InvalidApplicationException, DatabaseException {
        if (interview == null || interview.getId() <= 0) {
            throw new InvalidApplicationException("Invalid interview for update.");
        }
        validateInterview(interview);
        return interviewDAO.update(interview);
    }

    public boolean cancelInterview(int interviewId) throws DatabaseException {
        return interviewDAO.updateInterviewStatus(interviewId, InterviewStatus.CANCELLED);
    }

    public boolean completeInterview(int interviewId) throws DatabaseException {
        return interviewDAO.updateInterviewStatus(interviewId, InterviewStatus.COMPLETED);
    }

    private void validateInterview(Interview interview) throws InvalidApplicationException, DatabaseException {
        if (interview == null) {
            throw new InvalidApplicationException("Interview object cannot be null.");
        }
        Application app = applicationDAO.findById(interview.getApplicationId());
        if (app == null) {
            throw new InvalidApplicationException("Associated Application does not exist for ID: " + interview.getApplicationId());
        }
        if (interview.getInterviewDate() == null) {
            throw new InvalidApplicationException("Interview date is required.");
        }
        if (interview.getInterviewTime() == null) {
            throw new InvalidApplicationException("Interview time is required.");
        }
        if (interview.getInterviewType() == null) {
            throw new InvalidApplicationException("Interview type is required.");
        }
        if (interview.getInterviewType() == InterviewType.ONLINE &&
                (interview.getMeetingLink() == null || interview.getMeetingLink().trim().isEmpty())) {
            throw new InvalidApplicationException("Online interview requires a valid meeting link.");
        }
        if (interview.getInterviewType() == InterviewType.OFFLINE &&
                (interview.getLocation() == null || interview.getLocation().trim().isEmpty())) {
            throw new InvalidApplicationException("Offline interview requires a physical location address.");
        }
    }
}
