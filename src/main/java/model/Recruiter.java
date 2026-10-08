package model;

import exception.InvalidUserException;
import interfaces.JobOperations;
import interfaces.UserOperations;
import model.enums.UserRole;
import model.enums.UserStatus;

/**
 * Recruiter model class demonstrating Inheritance & Interface Implementation.
 */
public class Recruiter extends User implements UserOperations, JobOperations {

    private String phone;
    private String designation;

    public Recruiter() {
        super();
        setRole(UserRole.RECRUITER);
    }

    public Recruiter(int id, String name, String email, String passwordHash, UserStatus status,
                     String phone, String designation) throws InvalidUserException {
        super(id, name, email, passwordHash, UserRole.RECRUITER, status);
        this.phone = phone;
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String showDashboard() {
        return "Recruiter Dashboard";
    }

    // --- UserOperations Implementation ---
    @Override
    public void createProfile() {
        System.out.println("Creating profile for Recruiter: " + getName());
    }

    @Override
    public void updateProfile() {
        System.out.println("Updating profile for Recruiter: " + getName());
    }

    @Override
    public void viewProfile() {
        System.out.println("Viewing Recruiter profile: " + getName() + " [" + designation + "]");
    }

    // --- JobOperations Implementation ---
    @Override
    public void createJob() {
        System.out.println("Recruiter " + getName() + " posted a new job vacancy.");
    }

    @Override
    public void updateJob() {
        System.out.println("Recruiter " + getName() + " updated job posting.");
    }

    @Override
    public void deleteJob() {
        System.out.println("Recruiter " + getName() + " deleted job posting.");
    }

    @Override
    public String toString() {
        return "Recruiter{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + phone + '\'' +
                ", designation='" + designation + '\'' +
                '}';
    }
}
