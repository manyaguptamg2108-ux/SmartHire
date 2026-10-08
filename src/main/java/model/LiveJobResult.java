package model;

import java.io.Serializable;

/**
 * Domain model representing a real live job listing retrieved from an external job search provider (e.g. Adzuna API).
 */
public class LiveJobResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String externalId;
    private String title;
    private String companyName;
    private String location;
    private String description;
    private Double salaryMin;
    private Double salaryMax;
    private String jobType;
    private String category;
    private String createdDate;
    private String sourceName;
    private String redirectUrl;

    public LiveJobResult() {
        this.sourceName = "Adzuna Live Jobs";
    }

    public LiveJobResult(String externalId, String title, String companyName, String location,
                         String description, Double salaryMin, Double salaryMax, String jobType,
                         String category, String createdDate, String sourceName, String redirectUrl) {
        this.externalId = externalId;
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.description = description;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.jobType = jobType;
        this.category = category;
        this.createdDate = createdDate;
        this.sourceName = (sourceName != null && !sourceName.trim().isEmpty()) ? sourceName : "Adzuna Live Jobs";
        this.redirectUrl = redirectUrl;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Double salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Double getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Double salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getFormattedSalary() {
        if (salaryMin == null && salaryMax == null) {
            return "Salary Not Specified";
        }
        if (salaryMin != null && salaryMax != null && salaryMin.equals(salaryMax)) {
            return String.format("₹ %,.0f / yr", salaryMin);
        }
        if (salaryMin != null && salaryMax != null) {
            return String.format("₹ %,.0f - ₹ %,.0f / yr", salaryMin, salaryMax);
        }
        if (salaryMin != null) {
            return String.format("From ₹ %,.0f / yr", salaryMin);
        }
        return String.format("Up to ₹ %,.0f / yr", salaryMax);
    }

    @Override
    public String toString() {
        return "LiveJobResult{" +
                "externalId='" + externalId + '\'' +
                ", title='" + title + '\'' +
                ", companyName='" + companyName + '\'' +
                ", location='" + location + '\'' +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
