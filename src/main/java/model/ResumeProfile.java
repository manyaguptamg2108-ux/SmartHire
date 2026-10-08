package model;

import java.io.Serializable;
import java.util.*;

/**
 * Domain model representing structured candidate resume data extracted from uploaded PDF/DOCX resumes.
 */
public class ResumeProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String phone;
    private Set<String> skills;
    private List<String> education;
    private List<String> experience;
    private Set<String> extractedKeywords;
    private String summary;
    private String rawText;

    public ResumeProfile() {
        this.skills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.education = new ArrayList<>();
        this.experience = new ArrayList<>();
        this.extractedKeywords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    }

    public ResumeProfile(String name, String email, String phone, Set<String> skills,
                         List<String> education, List<String> experience,
                         Set<String> extractedKeywords, String summary, String rawText) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        if (skills != null) this.skills.addAll(skills);
        if (education != null) this.education.addAll(education);
        if (experience != null) this.experience.addAll(experience);
        if (extractedKeywords != null) this.extractedKeywords.addAll(extractedKeywords);
        this.summary = summary;
        this.rawText = rawText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (skills != null) {
            this.skills.addAll(skills);
        }
    }

    public void addSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            this.skills.add(skill.trim());
        }
    }

    public List<String> getEducation() {
        return education;
    }

    public void setEducation(List<String> education) {
        this.education = education != null ? education : new ArrayList<>();
    }

    public void addEducation(String edu) {
        if (edu != null && !edu.trim().isEmpty()) {
            this.education.add(edu.trim());
        }
    }

    public List<String> getExperience() {
        return experience;
    }

    public void setExperience(List<String> experience) {
        this.experience = experience != null ? experience : new ArrayList<>();
    }

    public void addExperience(String exp) {
        if (exp != null && !exp.trim().isEmpty()) {
            this.experience.add(exp.trim());
        }
    }

    public Set<String> getExtractedKeywords() {
        return extractedKeywords;
    }

    public void setExtractedKeywords(Set<String> extractedKeywords) {
        this.extractedKeywords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (extractedKeywords != null) {
            this.extractedKeywords.addAll(extractedKeywords);
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    @Override
    public String toString() {
        return "ResumeProfile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", skillsCount=" + skills.size() +
                ", educationCount=" + education.size() +
                ", experienceCount=" + experience.size() +
                '}';
    }
}
