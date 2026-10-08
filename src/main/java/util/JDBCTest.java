package util;

import exception.DatabaseException;
import dao.UserDAO;
import dao.JobDAO;
import dao.SkillDAO;
import model.User;
import model.Job;
import model.Skill;

import java.sql.Connection;
import java.util.List;

/**
 * JDBC Connection & Resource Verification Test.
 */
public class JDBCTest {

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("SMART HIRE JDBC TEST");
        System.out.println("=================================\n");

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection: SUCCESS\n");
            } else {
                System.err.println("Database connection: FAILED\n");
                return;
            }

            UserDAO userDAO = new UserDAO();
            List<User> users = userDAO.findAll();
            System.out.println("Users found: " + users.size());

            JobDAO jobDAO = new JobDAO();
            List<Job> jobs = jobDAO.findAll();
            System.out.println("Jobs found: " + jobs.size());

            SkillDAO skillDAO = new SkillDAO();
            List<Skill> skills = skillDAO.findAll();
            System.out.println("Skills found: " + skills.size());

            System.out.println("\nJDBC test completed successfully.\n");
            System.out.println("=================================");

        } catch (DatabaseException e) {
            System.err.println("Database Exception during JDBC test: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(conn);
        }
    }
}
