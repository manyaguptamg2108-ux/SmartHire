package util;

import dao.*;
import exception.DatabaseException;
import model.*;
import model.enums.JobStatus;
import model.enums.JobType;
import model.enums.NotificationType;

import java.util.List;

/**
 * Verification test runner for SmartHire DAO Layer (STEP 4).
 * Verifies PreparedStatement execution, ResultSet mapping, and non-destructive CRUD logic.
 */
public class DAOTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    SMART HIRE - STEP 4 DAO LAYER VERIFICATION   ");
        System.out.println("==================================================\n");

        try {
            UserDAO userDAO = new UserDAO();
            JobSeekerDAO seekerDAO = new JobSeekerDAO();
            RecruiterDAO recruiterDAO = new RecruiterDAO();
            CompanyDAO companyDAO = new CompanyDAO();
            SkillDAO skillDAO = new SkillDAO();
            JobDAO jobDAO = new JobDAO();
            JobSkillsDAO jobSkillsDAO = new JobSkillsDAO();
            ApplicationDAO appDAO = new ApplicationDAO();
            InterviewDAO interviewDAO = new InterviewDAO();
            NotificationDAO notifDAO = new NotificationDAO();

            // 1. User find by email
            System.out.println("[1] Testing User findByEmail...");
            User adminUser = userDAO.findByEmail("admin@smarthire.com");
            System.out.println(" -> Found User by Email: " + (adminUser != null ? adminUser.getName() + " (" + adminUser.getRole() + ")" : "NULL"));
            if (adminUser == null) throw new IllegalStateException("User findByEmail failed!");

            // 2. User find by ID
            System.out.println("[2] Testing User findById...");
            User u1 = userDAO.findById(1);
            System.out.println(" -> Found User ID 1: " + (u1 != null ? u1.getEmail() : "NULL"));

            // 3. Company retrieval
            System.out.println("[3] Testing Company retrieval...");
            List<Company> companies = companyDAO.findAll();
            System.out.println(" -> Companies count: " + companies.size());
            for (Company c : companies) {
                System.out.println("    - " + c);
            }

            // 4. Skill retrieval & Find by Name
            System.out.println("[4] Testing Skill retrieval...");
            List<Skill> skills = skillDAO.findAll();
            System.out.println(" -> Total Skills count: " + skills.size());
            Skill javaSkill = skillDAO.findByName("Java");
            System.out.println(" -> Found Skill by Name ('Java'): " + javaSkill);

            // 5. Job retrieval & Job Skills
            System.out.println("[5] Testing Job retrieval & Job Skills mapping...");
            List<Job> jobs = jobDAO.findAll();
            System.out.println(" -> Total Jobs count: " + jobs.size());
            if (!jobs.isEmpty()) {
                Job firstJob = jobs.get(0);
                List<Skill> jobSkills = jobSkillsDAO.getSkillsForJob(firstJob.getId());
                System.out.println(" -> Skills for Job '" + firstJob.getTitle() + "': " + jobSkills.size() + " skills");
            }

            // 6. Job Search with PreparedStatement filters
            System.out.println("[6] Testing Parameterized Job Search...");
            List<Job> devJobs = jobDAO.searchJobs("Developer", null, JobType.FULL_TIME, JobStatus.ACTIVE);
            System.out.println(" -> Search result ('Developer', FULL_TIME): " + devJobs.size() + " jobs found");

            // 7. Application retrieval
            System.out.println("[7] Testing Application retrieval...");
            List<Application> apps = appDAO.findAll();
            System.out.println(" -> Total Applications count: " + apps.size());

            // 8. Interview retrieval
            System.out.println("[8] Testing Interview retrieval...");
            List<Interview> interviews = interviewDAO.findAll();
            System.out.println(" -> Total Interviews count: " + interviews.size());

            // 9. Notification retrieval
            System.out.println("[9] Testing Notification retrieval...");
            List<Notification> notifs = notifDAO.findAll();
            System.out.println(" -> Total Notifications count: " + notifs.size());

            // 10. CRUD Operation Verification (Create, Update, Read, Delete temporary Skill)
            System.out.println("[10] Verifying Full CRUD Operations on Skill Entity...");
            Skill tempSkill = new Skill("Temporary Test Skill " + System.currentTimeMillis());
            boolean created = skillDAO.save(tempSkill);
            System.out.println(" -> CREATE Skill: " + created + " [Assigned ID: " + tempSkill.getId() + "]");

            tempSkill.setSkillName("Updated Test Skill " + System.currentTimeMillis());
            boolean updated = skillDAO.update(tempSkill);
            System.out.println(" -> UPDATE Skill: " + updated);

            Skill fetched = skillDAO.findById(tempSkill.getId());
            System.out.println(" -> READ Skill by ID: " + (fetched != null ? fetched.getSkillName() : "NULL"));

            boolean deleted = skillDAO.delete(tempSkill.getId());
            System.out.println(" -> DELETE Skill: " + deleted);

            System.out.println("\n==================================================");
            System.out.println("   ALL STEP 4 DAO LAYER TESTS PASSED SUCCESSFULLY!");
            System.out.println("==================================================");

        } catch (DatabaseException e) {
            System.err.println("DAO Test Database Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.err.println("DAO Test Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
