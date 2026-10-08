package dao;

import exception.DatabaseException;
import model.Skill;

public interface SkillDAOInterface extends GenericDAO<Skill> {
    Skill findByName(String skillName) throws DatabaseException;
}
