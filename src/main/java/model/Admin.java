package model;

import exception.InvalidUserException;
import model.enums.UserRole;
import model.enums.UserStatus;

/**
 * Admin model class demonstrating Inheritance & Polymorphism.
 */
public class Admin extends User {

    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }

    public Admin(int id, String name, String email, String passwordHash, UserStatus status) throws InvalidUserException {
        super(id, name, email, passwordHash, UserRole.ADMIN, status);
    }

    @Override
    public String showDashboard() {
        return "Admin Dashboard";
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
