package dao;

import exception.DatabaseException;
import model.Skill;
import java.util.List;

public interface JobSkillsDAOInterface {
    boolean addSkillToJob(int jobId, int skillId) throws DatabaseException;
    boolean removeSkillFromJob(int jobId, int skillId) throws DatabaseException;
    List<Skill> getSkillsForJob(int jobId) throws DatabaseException;
    boolean removeAllSkillsFromJob(int jobId) throws DatabaseException;
}
