package dao;

import exception.DatabaseException;
import model.Interview;
import model.enums.InterviewStatus;
import java.util.List;

public interface InterviewDAOInterface extends GenericDAO<Interview> {
    List<Interview> findByApplicationId(int applicationId) throws DatabaseException;
    boolean updateInterviewStatus(int interviewId, InterviewStatus status) throws DatabaseException;
}
