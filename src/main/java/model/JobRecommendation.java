package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a ranked job recommendation item matching a candidate's uploaded resume.
 */
public class JobRecommendation implements Serializable, Comparable<JobRecommendation> {
    private static final long serialVersionUID = 1L;

    private boolean isExternal;
    private Job localJob;
    private LiveJobResult liveJob;

    private String jobTitle;
    private String companyName;
    private String location;
    private String salaryText;
    private double matchPercentage;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String matchExplanation;
    private String actionUrl;

    public JobRecommendation() {
        this.matchedSkills = new ArrayList<>();
        this.missingSkills = new ArrayList<>();
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public Job getLocalJob() {
        return localJob;
    }

    public void setLocalJob(Job localJob) {
        this.localJob = localJob;
    }

    public LiveJobResult getLiveJob() {
        return liveJob;
    }

    public void setLiveJob(LiveJobResult liveJob) {
        this.liveJob = liveJob;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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

    public String getSalaryText() {
        return salaryText;
    }

    public void setSalaryText(String salaryText) {
        this.salaryText = salaryText;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills != null ? matchedSkills : new ArrayList<>();
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills != null ? missingSkills : new ArrayList<>();
    }

    public String getMatchExplanation() {
        return matchExplanation;
    }

    public void setMatchExplanation(String matchExplanation) {
        this.matchExplanation = matchExplanation;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    @Override
    public int compareTo(JobRecommendation o) {
        return Double.compare(o.matchPercentage, this.matchPercentage); // Descending by match %
    }
}
