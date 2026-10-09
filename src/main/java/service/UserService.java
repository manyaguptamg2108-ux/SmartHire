package service;

import dao.*;
import exception.AuthenticationException;
import exception.DatabaseException;
import exception.InvalidUserException;
import model.*;
import model.enums.UserRole;
import model.enums.UserStatus;
import util.DBUtil;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service layer managing User authentication, registration rules, and account status.
 */
public class UserService {

    private final UserDAOInterface userDAO;
    private final JobSeekerDAOInterface jobSeekerDAO;
    private final RecruiterDAOInterface recruiterDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.jobSeekerDAO = new JobSeekerDAO();
        this.recruiterDAO = new RecruiterDAO();
    }

    public UserService(UserDAOInterface userDAO, JobSeekerDAOInterface jobSeekerDAO, RecruiterDAOInterface recruiterDAO) {
        this.userDAO = userDAO;
        this.jobSeekerDAO = jobSeekerDAO;
        this.recruiterDAO = recruiterDAO;
    }

    /**
     * Authenticates a user with email and password.
     *
     * @param email User email address
     * @param password Raw password string
     * @return Authenticated User object
     * @throws AuthenticationException if credentials are invalid or user is inactive
     * @throws DatabaseException if persistence error occurs
     */
    public User loginUser(String email, String password) throws AuthenticationException, DatabaseException {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Invalid email or password.");
        }

        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            throw new AuthenticationException("Invalid email or password.");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AuthenticationException("Account is inactive. Please contact system administrator.");
        }

        boolean validPassword = PasswordUtil.verifyPassword(password, user.getPasswordHash());
        if (!validPassword) {
            throw new AuthenticationException("Invalid email or password.");
        }

        return user;
    }

    /**
     * Registers a new user enforcing public registration role rules and password hashing.
     */
    public boolean registerUser(String name, String email, String password, String confirmPassword, UserRole role)
            throws InvalidUserException, DatabaseException {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserException("Full Name is required.");
        }
        if (email == null || !email.contains("@") || email.trim().isEmpty()) {
            throw new InvalidUserException("Valid Email address is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidUserException("Password is required.");
        }
        if (password.length() < 6) {
            throw new InvalidUserException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new InvalidUserException("Passwords do not match.");
        }
        if (role == null) {
            throw new InvalidUserException("User role is required.");
        }

        // Security rule: ADMIN cannot be registered publicly
        if (role == UserRole.ADMIN) {
            throw new InvalidUserException("Registration as ADMIN is strictly prohibited.");
        }

        // Email uniqueness validation
        User existing = userDAO.findByEmail(email.trim().toLowerCase());
        if (existing != null) {
            throw new InvalidUserException("This email is already registered. Please sign in.");
        }

        String hashedPassword;
        if (password != null && password.length() == 64 && password.matches("^[0-9a-fA-F]{64}$")) {
            hashedPassword = password;
        } else {
            hashedPassword = PasswordUtil.hashPassword(password);
        }
        User user;

        if (role == UserRole.JOB_SEEKER) {
            user = new JobSeeker(0, name.trim(), email.trim().toLowerCase(), hashedPassword, UserStatus.ACTIVE,
                    "", "", "", "", "", "", 0);
        } else {
            user = new Recruiter(0, name.trim(), email.trim().toLowerCase(), hashedPassword, UserStatus.ACTIVE,
                    "", "");
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            boolean userSaved;
            if (userDAO instanceof UserDAO) {
                userSaved = ((UserDAO) userDAO).save(user, conn);
            } else {
                userSaved = userDAO.save(user);
            }

            if (!userSaved || user.getId() <= 0) {
                conn.rollback();
                return false;
            }

            boolean profileSaved = true;
            if (role == UserRole.JOB_SEEKER && user instanceof JobSeeker) {
                if (jobSeekerDAO instanceof JobSeekerDAO) {
                    profileSaved = ((JobSeekerDAO) jobSeekerDAO).save((JobSeeker) user, conn);
                } else {
                    profileSaved = jobSeekerDAO.save((JobSeeker) user);
                }
            } else if (role == UserRole.RECRUITER && user instanceof Recruiter) {
                if (recruiterDAO instanceof RecruiterDAO) {
                    profileSaved = ((RecruiterDAO) recruiterDAO).save((Recruiter) user, conn);
                } else {
                    profileSaved = recruiterDAO.save((Recruiter) user);
                }
            }

            if (!profileSaved) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }
            throw new DatabaseException("Error registering user: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public boolean registerUser(User user) throws InvalidUserException, DatabaseException {
        if (user == null) {
            throw new InvalidUserException("User object cannot be null.");
        }
        return registerUser(user.getName(), user.getEmail(), user.getPasswordHash(), user.getPasswordHash(), user.getRole());
    }

    public User findUserById(int id) throws DatabaseException {
        return userDAO.findById(id);
    }

    public User findUserByEmail(String email) throws DatabaseException {
        if (email == null) return null;
        return userDAO.findByEmail(email.trim().toLowerCase());
    }

    public List<User> getAllUsers() throws DatabaseException {
        return userDAO.findAll();
    }

    public boolean updateUser(User user) throws InvalidUserException, DatabaseException {
        if (user == null || user.getId() <= 0) {
            throw new InvalidUserException("Invalid user for update.");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new InvalidUserException("User name cannot be empty.");
        }
        return userDAO.update(user);
    }

    public boolean deactivateUser(int userId) throws DatabaseException {
        return userDAO.updateStatus(userId, UserStatus.INACTIVE);
    }

    public boolean activateUser(int userId) throws DatabaseException {
        return userDAO.updateStatus(userId, UserStatus.ACTIVE);
    }
}
