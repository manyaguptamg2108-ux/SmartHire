package service;

import dao.JobSeekerDAO;
import dao.JobSeekerDAOInterface;
import dao.UserDAO;
import dao.UserDAOInterface;
import exception.DatabaseException;
import exception.InvalidUserException;
import model.JobSeeker;
import model.User;

import java.util.List;

/**
 * Service managing Job Seeker profiles and profile completion calculation.
 */
public class JobSeekerService {

    private final JobSeekerDAOInterface jobSeekerDAO;
    private final UserDAOInterface userDAO;

    public JobSeekerService() {
        this.jobSeekerDAO = new JobSeekerDAO();
        this.userDAO = new UserDAO();
    }

    public JobSeekerService(JobSeekerDAOInterface jobSeekerDAO, UserDAOInterface userDAO) {
        this.jobSeekerDAO = jobSeekerDAO;
        this.userDAO = userDAO;
    }

    public boolean createProfile(JobSeeker seeker) throws InvalidUserException, DatabaseException {
        if (seeker == null) {
            throw new InvalidUserException("JobSeeker profile cannot be null.");
        }
        User user = userDAO.findById(seeker.getId());
        if (user == null) {
            throw new InvalidUserException("Associated user account does not exist.");
        }

        int completion = calculateProfileCompletion(seeker);
        seeker.setProfileCompletion(completion);

        return jobSeekerDAO.save(seeker);
    }

    public JobSeeker getProfileById(int id) throws DatabaseException {
        return jobSeekerDAO.findById(id);
    }

    public JobSeeker getProfileByUserId(int userId) throws DatabaseException {
        return jobSeekerDAO.findByUserId(userId);
    }

    public List<JobSeeker> getAllProfiles() throws DatabaseException {
        return jobSeekerDAO.findAll();
    }

    public boolean updateProfile(JobSeeker seeker) throws InvalidUserException, DatabaseException {
        if (seeker == null || seeker.getId() <= 0) {
            throw new InvalidUserException("Invalid JobSeeker profile for update.");
        }

        int completion = calculateProfileCompletion(seeker);
        seeker.setProfileCompletion(completion);

        return jobSeekerDAO.update(seeker);
    }

    /**
     * Calculates actual profile completion percentage based on filled fields.
     * Phone: +15%, Education: +20%, Experience: +20%, Location: +15%, Bio: +15%, Resume: +15%.
     */
    public int calculateProfileCompletion(JobSeeker seeker) {
        if (seeker == null) return 0;
        int score = 0;

        if (seeker.getPhone() != null && !seeker.getPhone().trim().isEmpty()) score += 15;
        if (seeker.getEducation() != null && !seeker.getEducation().trim().isEmpty()) score += 20;
        if (seeker.getExperience() != null && !seeker.getExperience().trim().isEmpty()) score += 20;
        if (seeker.getLocation() != null && !seeker.getLocation().trim().isEmpty()) score += 15;
        if (seeker.getBio() != null && !seeker.getBio().trim().isEmpty()) score += 15;
        if (seeker.getResumePath() != null && !seeker.getResumePath().trim().isEmpty()) score += 15;

        return Math.min(100, score);
    }
}
