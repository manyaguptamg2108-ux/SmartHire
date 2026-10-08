package dao;

import exception.DatabaseException;
import model.Job;
import model.enums.JobStatus;
import model.enums.JobType;
import java.util.List;

public interface JobDAOInterface extends GenericDAO<Job> {
    List<Job> findByCompanyId(int companyId) throws DatabaseException;
    boolean updateJobStatus(int jobId, JobStatus status) throws DatabaseException;
    List<Job> searchJobs(String keyword, String location, JobType jobType, JobStatus status) throws DatabaseException;
}
