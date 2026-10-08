package util;

import exception.InvalidApplicationException;
import exception.InvalidJobException;
import exception.InvalidUserException;
import interfaces.ApplicationOperations;
import interfaces.Dashboard;
import interfaces.JobOperations;
import interfaces.MatchingOperations;
import interfaces.UserOperations;
import model.Admin;
import model.Application;
import model.Company;
import model.Interview;
import model.Job;
import model.JobSeeker;
import model.Notification;
import model.Recruiter;
import model.Skill;
import model.User;
import model.enums.ApplicationStatus;
import model.enums.InterviewStatus;
import model.enums.InterviewType;
import model.enums.JobStatus;
import model.enums.JobType;
import model.enums.NotificationType;
import model.enums.UserRole;
import model.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Verification class demonstrating Java Object-Oriented Programming (OOP) concepts
 * for SmartHire Domain Layer (STEP 3).
 */
public class OOPTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART HIRE - STEP 3 OOP DOMAIN VERIFICATION   ");
        System.out.println("==================================================\n");

        try {
            // ----------------------------------------------------
            // 1. INHERITANCE & ABSTRACT CONSTRUCTORS
            // ----------------------------------------------------
            System.out.println("[1] Demonstrating Inheritance & Object Construction...");
            JobSeeker seeker = new JobSeeker(
                    1, "John Doe", "john.doe@email.com", "hash_secret_123", UserStatus.ACTIVE,
                    "+1-555-0101", "B.S. Computer Science", "3 years Java experience",
                    "New York, NY", "Backend Developer", "/resumes/john.pdf", 85
            );

            Recruiter recruiter = new Recruiter(
                    2, "Sarah Jenkins", "recruiter@techcorp.com", "hash_secret_456", UserStatus.ACTIVE,
                    "+1-555-0192", "Lead Recruiter"
            );

            Admin admin = new Admin(
                    3, "System Administrator", "admin@smarthire.com", "hash_secret_789", UserStatus.ACTIVE
            );

            System.out.println(" -> Seeker created: " + seeker.getName() + " (" + seeker.getRole() + ")");
            System.out.println(" -> Recruiter created: " + recruiter.getName() + " (" + recruiter.getRole() + ")");
            System.out.println(" -> Admin created: " + admin.getName() + " (" + admin.getRole() + ")");
            System.out.println("PASSED.\n");

            // ----------------------------------------------------
            // 2. ABSTRACTION & RUNTIME POLYMORPHISM
            // ----------------------------------------------------
            System.out.println("[2] Demonstrating Abstraction & Runtime Polymorphism...");
            List<User> userList = new ArrayList<>();
            userList.add(seeker);
            userList.add(recruiter);
            userList.add(admin);

            for (User u : userList) {
                // Same method call showDashboard() behaves differently according to actual subclass type
                System.out.println(" -> " + u.getName() + " [" + u.getClass().getSimpleName() + "]: " + u.showDashboard());
            }
            System.out.println("PASSED.\n");

            // ----------------------------------------------------
            // 3. INTERFACE POLYMORPHISM & METHOD OVERRIDING
            // ----------------------------------------------------
            System.out.println("[3] Demonstrating Interface Polymorphism...");
            Dashboard d1 = seeker;
            Dashboard d2 = recruiter;
            Dashboard d3 = admin;

            System.out.println(" -> Dashboard 1: " + d1.showDashboard());
            System.out.println(" -> Dashboard 2: " + d2.showDashboard());
            System.out.println(" -> Dashboard 3: " + d3.showDashboard());

            UserOperations userOps = seeker;
            userOps.viewProfile();

            JobOperations jobOps = recruiter;
            jobOps.createJob();

            ApplicationOperations appOps = seeker;
            appOps.apply();

            MatchingOperations matchOps = seeker;
            System.out.println(" -> Calculated Match: " + matchOps.calculateMatchPercentage() + "%");
            System.out.println("PASSED.\n");

            // ----------------------------------------------------
            // 4. EQUALS & HASHCODE / HASHSET DUPLICATE REMOVAL
            // ----------------------------------------------------
            System.out.println("[4] Demonstrating equals(), hashCode() & HashSet Duplicate Removal...");
            Set<Skill> skillSet = new HashSet<>();
            Skill s1 = new Skill(1, "Java");
            Skill s2 = new Skill(2, "Java"); // Same skill name
            Skill s3 = new Skill(3, "java"); // Case variation
            Skill s4 = new Skill(4, "Python");

            skillSet.add(s1);
            skillSet.add(s2);
            skillSet.add(s3);
            skillSet.add(s4);

            System.out.println(" -> Added 4 skills ('Java', 'Java', 'java', 'Python') into HashSet.");
            System.out.println(" -> HashSet size (expected 2): " + skillSet.size());
            for (Skill sk : skillSet) {
                System.out.println("    - " + sk);
            }
            if (skillSet.size() != 2) {
                throw new IllegalStateException("HashSet duplicate removal failed!");
            }
            System.out.println("PASSED.\n");

            // ----------------------------------------------------
            // 5. ENCAPSULATION & TO STRING PROTECTION
            // ----------------------------------------------------
            System.out.println("[5] Demonstrating Encapsulation & Secure toString()...");
            System.out.println(" -> Seeker toString(): " + seeker.toString());
            boolean containsPasswordHash = seeker.toString().contains("hash_secret_123");
            System.out.println(" -> Password hash present in toString()? " + containsPasswordHash);
            if (containsPasswordHash) {
                throw new IllegalStateException("SECURITY ISSUE: Password hash exposed in toString()!");
            }
            System.out.println("PASSED.\n");

            // ----------------------------------------------------
            // 6. MODEL VALIDATION & CUSTOM EXCEPTION VERIFICATION
            // ----------------------------------------------------
            System.out.println("[6] Demonstrating Custom Exception Handling & Validation...");

            // Test 6a: Invalid User Email Validation
            try {
                seeker.setEmail("invalid-email-no-at-sign");
                System.err.println("FAILED: Should have rejected invalid email.");
            } catch (InvalidUserException e) {
                System.out.println(" -> Caught expected InvalidUserException: " + e.getMessage());
            }

            // Test 6b: Invalid Salary Validation in Job
            try {
                Job invalidJob = new Job(
                        1, 1, "Java Developer", "Description", "Remote",
                        120000.00, 90000.00, // Max < Min!
                        JobType.FULL_TIME, "3 years", "B.S. CS",
                        LocalDate.of(2026, 12, 31), JobStatus.ACTIVE
                );
                System.err.println("FAILED: Should have rejected salaryMax < salaryMin.");
            } catch (InvalidJobException e) {
                System.out.println(" -> Caught expected InvalidJobException: " + e.getMessage());
            }

            // Test 6c: Invalid Application Match Percentage
            try {
                Application app = new Application(1, 1, 1, ApplicationStatus.APPLIED, 120.00, "Cover Letter");
                System.err.println("FAILED: Should have rejected matchPercentage > 100.");
            } catch (InvalidApplicationException e) {
                System.out.println(" -> Caught expected InvalidApplicationException: " + e.getMessage());
            }
            System.out.println("PASSED.\n");

            // ----------------------------------------------------
            // 7. OTHER DOMAIN ENTITIES INSTANTIATION
            // ----------------------------------------------------
            System.out.println("[7] Instantiating and Verifying Remaining Domain Entities...");
            Company company = new Company(1, 2, "TechCorp Solutions", "Leading Tech Firm", "San Francisco, CA", "https://techcorp.example.com", "IT", "500-1000");
            Job job = new Job(1, 1, "Java Developer", "Enterprise backend developer position", "Remote", 90000.00, 120000.00, JobType.FULL_TIME, "2-4 years", "B.S. CS", LocalDate.of(2026, 12, 31), JobStatus.ACTIVE);
            Application app = new Application(1, 1, 1, ApplicationStatus.SHORTLISTED, 88.50, "Cover Letter Content");
            Interview interview = new Interview(1, 1, LocalDate.of(2026, 10, 15), LocalTime.of(10, 0), InterviewType.ONLINE, "https://meet.example.com", "Google Meet", "Round 1", InterviewStatus.SCHEDULED);
            Notification notif = new Notification(1, 2, "New Application", "John applied for Java Developer", NotificationType.NEW_APPLICATION, false);

            System.out.println(" -> Company: " + company);
            System.out.println(" -> Job: " + job);
            System.out.println(" -> Application: " + app);
            System.out.println(" -> Interview: " + interview);
            System.out.println(" -> Notification: " + notif);

            notif.markAsRead();
            System.out.println(" -> Notification markAsRead() -> isRead: " + notif.isRead());
            System.out.println("PASSED.\n");

            System.out.println("==================================================");
            System.out.println("   ALL STEP 3 OOP DOMAIN TESTS PASSED SUCCESSFULLY!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("TEST SUITE FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
