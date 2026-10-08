package service;

import dao.RecruiterDAO;
import dao.RecruiterDAOInterface;
import dao.UserDAO;
import dao.UserDAOInterface;
import exception.DatabaseException;
import exception.InvalidUserException;
import model.Recruiter;
import model.User;

import java.util.List;

/**
 * Service managing Recruiter profiles and business rules.
 */
public class RecruiterService {

    private final RecruiterDAOInterface recruiterDAO;
    private final UserDAOInterface userDAO;

    public RecruiterService() {
        this.recruiterDAO = new RecruiterDAO();
        this.userDAO = new UserDAO();
    }

    public RecruiterService(RecruiterDAOInterface recruiterDAO, UserDAOInterface userDAO) {
        this.recruiterDAO = recruiterDAO;
        this.userDAO = userDAO;
    }

    public boolean createRecruiterProfile(Recruiter recruiter) throws InvalidUserException, DatabaseException {
        if (recruiter == null) {
            throw new InvalidUserException("Recruiter profile cannot be null.");
        }
        User user = userDAO.findById(recruiter.getId());
        if (user == null) {
            throw new InvalidUserException("Associated user account does not exist.");
        }
        if (recruiter.getDesignation() == null || recruiter.getDesignation().trim().isEmpty()) {
            throw new InvalidUserException("Recruiter designation is required.");
        }

        return recruiterDAO.save(recruiter);
    }

    public Recruiter getById(int id) throws DatabaseException {
        return recruiterDAO.findById(id);
    }

    public Recruiter getByUserId(int userId) throws DatabaseException {
        return recruiterDAO.findByUserId(userId);
    }

    public List<Recruiter> getAllRecruiters() throws DatabaseException {
        return recruiterDAO.findAll();
    }

    public boolean updateProfile(Recruiter recruiter) throws InvalidUserException, DatabaseException {
        if (recruiter == null || recruiter.getId() <= 0) {
            throw new InvalidUserException("Invalid Recruiter profile for update.");
        }
        if (recruiter.getDesignation() == null || recruiter.getDesignation().trim().isEmpty()) {
            throw new InvalidUserException("Recruiter designation is required.");
        }

        return recruiterDAO.update(recruiter);
    }
}
