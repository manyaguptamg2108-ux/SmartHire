package model;

import exception.InvalidUserException;

/**
 * Company model class representing an employer organization.
 */
public class Company {

    private int id;
    private int recruiterId;
    private String companyName;
    private String description;
    private String location;
    private String website;
    private String industry;
    private String companySize;

    public Company() {}

    public Company(int id, int recruiterId, String companyName, String description,
                   String location, String website, String industry, String companySize) throws InvalidUserException {
        this.id = id;
        this.recruiterId = recruiterId;
        setCompanyName(companyName);
        this.description = description;
        this.location = location;
        this.website = website;
        this.industry = industry;
        this.companySize = companySize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(int recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) throws InvalidUserException {
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new InvalidUserException("Company name cannot be empty.");
        }
        this.companyName = companyName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompanySize() {
        return companySize;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", industry='" + industry + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
