package model;

import exception.InvalidUserException;
import interfaces.ApplicationOperations;
import interfaces.MatchingOperations;
import interfaces.UserOperations;
import model.enums.UserRole;
import model.enums.UserStatus;

/**
 * Job Seeker model class demonstrating Inheritance & Interface Implementation.
 */
public class JobSeeker extends User implements UserOperations, ApplicationOperations, MatchingOperations {

    private String phone;
    private String education;
    private String experience;
    private String location;
    private String bio;
    private String resumePath;
    private int profileCompletion;

    public JobSeeker() {
        super();
        setRole(UserRole.JOB_SEEKER);
    }

    public JobSeeker(int id, String name, String email, String passwordHash, UserStatus status,
                     String phone, String education, String experience, String location,
                     String bio, String resumePath, int profileCompletion) throws InvalidUserException {
        super(id, name, email, passwordHash, UserRole.JOB_SEEKER, status);
        this.phone = phone;
        this.education = education;
        this.experience = experience;
        this.location = location;
        this.bio = bio;
        this.resumePath = resumePath;
        setProfileCompletion(profileCompletion);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }

    public int getProfileCompletion() {
        return profileCompletion;
    }

    public void setProfileCompletion(int profileCompletion) throws InvalidUserException {
        if (profileCompletion < 0 || profileCompletion > 100) {
            throw new InvalidUserException("Profile completion percentage must be between 0 and 100.");
        }
        this.profileCompletion = profileCompletion;
    }

    @Override
    public String showDashboard() {
        return "Job Seeker Dashboard";
    }

    // --- UserOperations Implementation ---
    @Override
    public void createProfile() {
        System.out.println("Creating profile for Job Seeker: " + getName());
    }

    @Override
    public void updateProfile() {
        System.out.println("Updating profile details for Job Seeker: " + getName());
    }

    @Override
    public void viewProfile() {
        System.out.println("Viewing profile of Job Seeker: " + getName() + " [Completion: " + profileCompletion + "%]");
    }

    // --- ApplicationOperations Implementation ---
    @Override
    public void apply() {
        System.out.println("Job Seeker " + getName() + " applied for job position.");
    }

    @Override
    public void updateStatus() {
        System.out.println("Job Seeker updated application status.");
    }

    // --- MatchingOperations Implementation ---
    @Override
    public double calculateMatchPercentage() {
        // Simple profile completion based calculation for OOP demonstration
        return (profileCompletion * 0.85);
    }

    @Override
    public String toString() {
        return "JobSeeker{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", profileCompletion=" + profileCompletion + "%" +
                '}';
    }
}
