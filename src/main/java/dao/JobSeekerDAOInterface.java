package dao;

import exception.DatabaseException;
import model.JobSeeker;

public interface JobSeekerDAOInterface extends GenericDAO<JobSeeker> {
    JobSeeker findByUserId(int userId) throws DatabaseException;
}
