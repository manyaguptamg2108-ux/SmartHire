package model;

import exception.InvalidUserException;
import interfaces.Dashboard;
import model.enums.UserRole;
import model.enums.UserStatus;
import java.util.Objects;

/**
 * Abstract Base Class for all System Users in SmartHire.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class User implements Dashboard {

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;
    private UserStatus status;

    public User() {
        this.status = UserStatus.ACTIVE;
    }

    public User(int id, String name, String email, String passwordHash, UserRole role, UserStatus status) throws InvalidUserException {
        setId(id);
        setName(name);
        setEmail(email);
        setPasswordHash(passwordHash);
        setRole(role);
        setStatus(status != null ? status : UserStatus.ACTIVE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidUserException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserException("User name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws InvalidUserException {
        if (email == null || !email.contains("@") || email.trim().isEmpty()) {
            throw new InvalidUserException("Invalid email format: " + email);
        }
        this.email = email.trim().toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) throws InvalidUserException {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new InvalidUserException("Password hash cannot be empty.");
        }
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * Abstract method demonstrating runtime polymorphism & abstraction.
     */
    @Override
    public abstract String showDashboard();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}
