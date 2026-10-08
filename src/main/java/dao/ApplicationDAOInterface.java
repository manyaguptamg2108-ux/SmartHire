package dao;

import exception.DatabaseException;
import model.Application;
import model.enums.ApplicationStatus;

import java.sql.Connection;
import java.util.List;

public interface ApplicationDAOInterface extends GenericDAO<Application> {
    List<Application> findByJobId(int jobId) throws DatabaseException;
    List<Application> findByJobSeekerId(int jobSeekerId) throws DatabaseException;
    boolean updateApplicationStatus(int applicationId, ApplicationStatus status) throws DatabaseException;
    boolean updateMatchPercentage(int applicationId, double matchPercentage) throws DatabaseException;
    boolean withdrawApplication(int applicationId) throws DatabaseException;
    
    // Transaction-aware save overload method using provided JDBC Connection
    boolean save(Connection conn, Application application) throws DatabaseException;
}
