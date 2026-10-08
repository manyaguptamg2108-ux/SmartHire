package service;

import dao.SkillDAO;
import dao.SkillDAOInterface;
import exception.DatabaseException;
import exception.InvalidUserException;
import model.Skill;

import java.util.List;

/**
 * Service managing skill catalog definitions and normalization.
 */
public class SkillService {

    private final SkillDAOInterface skillDAO;

    public SkillService() {
        this.skillDAO = new SkillDAO();
    }

    public SkillService(SkillDAOInterface skillDAO) {
        this.skillDAO = skillDAO;
    }

    public boolean createSkill(Skill skill) throws InvalidUserException, DatabaseException {
        if (skill == null || skill.getSkillName() == null || skill.getSkillName().trim().isEmpty()) {
            throw new InvalidUserException("Skill name cannot be empty.");
        }

        String normalized = skill.getSkillName().trim();
        skill.setSkillName(normalized);

        Skill existing = skillDAO.findByName(normalized);
        if (existing != null) {
            // Already exists in master catalog
            skill.setId(existing.getId());
            return true;
        }

        return skillDAO.save(skill);
    }

    public Skill getSkillById(int id) throws DatabaseException {
        return skillDAO.findById(id);
    }

    public Skill getSkillByName(String skillName) throws DatabaseException {
        if (skillName == null) return null;
        return skillDAO.findByName(skillName.trim());
    }

    public List<Skill> getAllSkills() throws DatabaseException {
        return skillDAO.findAll();
    }

    public boolean deleteSkill(int id) throws DatabaseException {
        return skillDAO.delete(id);
    }
}
