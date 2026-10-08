package dao;

import exception.DatabaseException;
import model.Recruiter;

public interface RecruiterDAOInterface extends GenericDAO<Recruiter> {
    Recruiter findByUserId(int userId) throws DatabaseException;
}
