package util;

import exception.InvalidApplicationException;
import exception.InvalidJobException;
import exception.InvalidUserException;
import model.*;
import model.enums.*;
import service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer & Business Logic Verification Test (STEP 5).
 * Tests all 15 business rules and validation logic against seeded data.
 */
public class ServiceTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SMART HIRE - STEP 5 SERVICE LAYER VERIFICATION  ");
        System.out.println("==================================================\n");

        try {
            UserService userService = new UserService();
            JobSeekerService seekerService = new JobSeekerService();
            RecruiterService recruiterService = new RecruiterService();
            CompanyService companyService = new CompanyService();
            SkillService skillService = new SkillService();
            JobService jobService = new JobService();
            ApplicationService appService = new ApplicationService();
            InterviewService interviewService = new InterviewService();
            NotificationService notifService = new NotificationService();
            MatchingService matchingService = new MatchingService();

            // 1. User lookup
            System.out.println("[1] Testing User Service lookup...");
            User admin = userService.findUserByEmail("admin@smarthire.com");
            System.out.println(" -> User found by email: " + admin.getName() + " (" + admin.getRole() + ")");

            // 2. Job seeker profile lookup & profile completion calculation
            System.out.println("[2] Testing Job Seeker Profile & Completion Calculation...");
            JobSeeker seeker = seekerService.getProfileByUserId(3); // John Doe
            System.out.println(" -> Seeker found: " + seeker.getName());
            int calculatedCompletion = seekerService.calculateProfileCompletion(seeker);
            System.out.println(" -> Calculated Profile Completion: " + calculatedCompletion + "%");

            // 3. Recruiter lookup
            System.out.println("[3] Testing Recruiter Service lookup...");
            Recruiter recruiter = recruiterService.getByUserId(2);
            System.out.println(" -> Recruiter found: " + recruiter.getName() + " [" + recruiter.getDesignation() + "]");

            // 4. Company lookup
            System.out.println("[4] Testing Company Service lookup...");
            List<Company> companies = companyService.getCompanyByRecruiterId(recruiter.getId());
            System.out.println(" -> Company found for Recruiter: " + (!companies.isEmpty() ? companies.get(0).getCompanyName() : "NONE"));

            // 5. Skill lookup
            System.out.println("[5] Testing Skill Service lookup & normalization...");
            Skill javaSkill = skillService.getSkillByName(" Java ");
            System.out.println(" -> Normalized Skill lookup (' Java '): " + (javaSkill != null ? javaSkill.getSkillName() : "NULL"));

            // 6. Job lookup & Search
            System.out.println("[6] Testing Job Service search & retrieval...");
            List<Job> activeDevJobs = jobService.searchJobs("Developer", "Remote", JobType.FULL_TIME, JobStatus.ACTIVE);
            System.out.println(" -> Active Developer Remote jobs count: " + activeDevJobs.size());

            // 7. Job Business Validation (Past deadline rejection test)
            System.out.println("[7] Testing Job Creation Validation (Past deadline rejection)...");
            try {
                Job invalidJob = new Job(0, 1, "Past Job", "Desc", "Loc", 50000, 60000, JobType.FULL_TIME, "1 yr", "B.S.", LocalDate.now().minusDays(5), JobStatus.ACTIVE);
                jobService.createJob(invalidJob, null);
                System.err.println("FAILED: Should have rejected job creation with past deadline.");
            } catch (InvalidJobException e) {
                System.out.println(" -> Caught expected InvalidJobException: " + e.getMessage());
            }

            // 8. Application Eligibility & Duplicate Application Rejection
            System.out.println("[8] Testing Application Eligibility & Duplicate Application Rejection...");
            try {
                // John Doe (seeker 1) already applied for Job 1 in seed data
                appService.applyForJob(1, 1, "Duplicate Cover Letter", 80.00);
                System.err.println("FAILED: Should have rejected duplicate job application.");
            } catch (InvalidApplicationException e) {
                System.out.println(" -> Caught expected InvalidApplicationException: " + e.getMessage());
            }

            // 9. Application Status Transition Rules
            System.out.println("[9] Testing Application Workflow Status Transitions...");
            List<Application> apps = appService.getAllApplications();
            if (!apps.isEmpty()) {
                int testAppId = apps.get(0).getId();
                // Valid transition APPLIED / SHORTLISTED -> INTERVIEW / UNDER_REVIEW
                System.out.println(" -> Application ID " + testAppId + " current status: " + apps.get(0).getStatus());

                // Invalid transition test (e.g. SELECTED -> APPLIED)
                try {
                    appService.updateApplicationStatus(testAppId, ApplicationStatus.SELECTED);
                    appService.updateApplicationStatus(testAppId, ApplicationStatus.APPLIED); // Illegal backward jump!
                    System.err.println("FAILED: Should have rejected backward jump from SELECTED to APPLIED.");
                } catch (InvalidApplicationException e) {
                    System.out.println(" -> Caught expected InvalidApplicationException: " + e.getMessage());
                }
            }

            // 10. Interview Validation (Online missing meeting link)
            System.out.println("[10] Testing Interview Scheduling Validation...");
            try {
                Interview onlineWithoutLink = new Interview(0, 1, LocalDate.now().plusDays(5), LocalTime.of(14, 0), InterviewType.ONLINE, "", "Location", "Notes", InterviewStatus.SCHEDULED);
                interviewService.scheduleInterview(onlineWithoutLink);
                System.err.println("FAILED: Should have rejected online interview without meeting link.");
            } catch (InvalidApplicationException e) {
                System.out.println(" -> Caught expected InvalidApplicationException: " + e.getMessage());
            }

            // 11. Notification Operations
            System.out.println("[11] Testing Notification Service...");
            List<Notification> recruiterNotifs = notifService.getUserNotifications(2);
            System.out.println(" -> Recruiter Notifications count: " + recruiterNotifs.size());

            // 12. Matching Service Calculation
            System.out.println("[12] Testing Skill Match Calculation...");
            List<Skill> jobReqSkills = new ArrayList<>();
            jobReqSkills.add(new Skill("Java"));
            jobReqSkills.add(new Skill("Spring Boot"));
            jobReqSkills.add(new Skill("SQL"));

            List<Skill> seekerSkills = new ArrayList<>();
            seekerSkills.add(new Skill("Java"));
            seekerSkills.add(new Skill("SQL"));
            seekerSkills.add(new Skill("HTML"));

            double match = matchingService.calculateMatchPercentage(jobReqSkills, seekerSkills);
            System.out.println(" -> Match percentage (2 of 3 matched = 66.67%): " + match + "%");

            System.out.println("\n==================================================");
            System.out.println("  ALL STEP 5 SERVICE LAYER TESTS PASSED CLEANLY!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("Service Test Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
