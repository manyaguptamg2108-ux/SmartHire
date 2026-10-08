package model;

import java.util.Objects;

/**
 * Skill entity demonstrating consistent equals() & hashCode() implementation
 * for Set-based duplicate removal.
 */
public class Skill {

    private int id;
    private String skillName;

    public Skill() {}

    public Skill(String skillName) {
        setSkillName(skillName);
    }

    public Skill(int id, String skillName) {
        this.id = id;
        setSkillName(skillName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = (skillName != null) ? skillName.trim() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(skillName.toLowerCase(), skill.skillName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillName.toLowerCase());
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", skillName='" + skillName + '\'' +
                '}';
    }
}
