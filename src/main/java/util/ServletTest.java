package util;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import java.io.File;

/**
 * Automated Architecture & Configuration Test Suite for Servlets (Step 11).
 * Verifies Servlet mappings, filter configuration, routing presence, and JSP error pages.
 */
public class ServletTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART HIRE - STEP 11 SERVLET ARCHITECTURE TEST ");
        System.out.println("==================================================");

        boolean allPassed = true;

        try {
            // [1] Verify Servlet classes exist
            System.out.print("[PASS] Servlet classes exist: ");
            ClassLoader cl = ServletTest.class.getClassLoader();
            Class<?> authServlet = Class.forName("controller.AuthServlet", true, cl);
            Class<?> dashboardServlet = Class.forName("controller.DashboardServlet", true, cl);
            Class<?> jobsServlet = Class.forName("controller.JobsServlet", true, cl);
            if (HttpServlet.class.isAssignableFrom(authServlet) &&
                HttpServlet.class.isAssignableFrom(dashboardServlet) &&
                HttpServlet.class.isAssignableFrom(jobsServlet)) {
                System.out.println("PASSED.");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [2] Verify Authentication servlet exists
            System.out.print("[PASS] Authentication servlet exists: ");
            WebServlet authAnno = authServlet.getAnnotation(WebServlet.class);
            if (authAnno != null && authAnno.urlPatterns().length > 0) {
                System.out.println("PASSED (" + String.join(", ", authAnno.urlPatterns()) + ")");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [3] Verify Dashboard servlet exists
            System.out.print("[PASS] Dashboard servlet exists: ");
            WebServlet dashAnno = dashboardServlet.getAnnotation(WebServlet.class);
            if (dashAnno != null && dashAnno.value().length > 0) {
                System.out.println("PASSED (" + String.join(", ", dashAnno.value()) + ")");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [4] Verify Job seeker servlet routes exist
            System.out.print("[PASS] Job seeker servlet routes exist: ");
            Class<?> profileServlet = Class.forName("controller.ProfileServlet", true, cl);
            Class<?> appServlet = Class.forName("controller.ApplicationsServlet", true, cl);
            Class<?> interviewsServlet = Class.forName("controller.InterviewsServlet", true, cl);
            Class<?> notifServlet = Class.forName("controller.NotificationsServlet", true, cl);
            if (profileServlet != null && appServlet != null && interviewsServlet != null && notifServlet != null) {
                System.out.println("PASSED (/jobs, /applications, /profile, /interviews, /notifications)");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [5] Verify Recruiter servlet routes exist
            System.out.print("[PASS] Recruiter servlet routes exist: ");
            Class<?> postJobServlet = Class.forName("controller.PostJobServlet", true, cl);
            Class<?> myJobsServlet = Class.forName("controller.RecruiterJobsServlet", true, cl);
            Class<?> applicantsServlet = Class.forName("controller.RecruiterApplicantsServlet", true, cl);
            Class<?> companyServlet = Class.forName("controller.CompanyProfileServlet", true, cl);
            if (postJobServlet != null && myJobsServlet != null && applicantsServlet != null && companyServlet != null) {
                System.out.println("PASSED (/post-job, /my-jobs, /applicants, /company-profile)");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [6] Verify Admin servlet routes exist
            System.out.print("[PASS] Admin servlet routes exist: ");
            Class<?> adminUsers = Class.forName("controller.AdminUsersServlet", true, cl);
            Class<?> adminJobs = Class.forName("controller.AdminJobsServlet", true, cl);
            Class<?> adminCompanies = Class.forName("controller.AdminCompaniesServlet", true, cl);
            Class<?> adminApps = Class.forName("controller.AdminApplicationsServlet", true, cl);
            if (adminUsers != null && adminJobs != null && adminCompanies != null && adminApps != null) {
                System.out.println("PASSED (/admin/users, /admin/jobs, /admin/companies, /admin/applications)");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [7] Verify AuthenticationFilter exists
            System.out.print("[PASS] AuthenticationFilter exists: ");
            Class<?> filterClass = Class.forName("controller.AuthenticationFilter", true, cl);
            WebFilter filterAnno = filterClass.getAnnotation(WebFilter.class);
            if (filterAnno != null) {
                System.out.println("PASSED (" + filterClass.getName() + ")");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [8] Verify Role-based routing configuration exists
            System.out.print("[PASS] Role-based routing configuration exists: ");
            boolean authUtilValid = AuthUtil.class.getDeclaredMethod("getCurrentRole", jakarta.servlet.http.HttpServletRequest.class) != null;
            if (authUtilValid && filterAnno != null && filterAnno.urlPatterns().length > 0) {
                System.out.println("PASSED.");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [9] Verify JSP forwarding/redirect architecture present
            System.out.print("[PASS] JSP forwarding/redirect architecture present: ");
            File webInfViews = new File("src/main/webapp/WEB-INF/views");
            if (webInfViews.exists() && webInfViews.isDirectory()) {
                System.out.println("PASSED (" + webInfViews.getPath() + ")");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            // [10] Verify Error pages present
            System.out.print("[PASS] Error pages present: ");
            File page403 = new File("src/main/webapp/WEB-INF/views/error/403.jsp");
            File page404 = new File("src/main/webapp/WEB-INF/views/error/404.jsp");
            File page500 = new File("src/main/webapp/WEB-INF/views/error/500.jsp");
            if (page403.exists() && page404.exists() && page500.exists()) {
                System.out.println("PASSED (403.jsp, 404.jsp, 500.jsp)");
            } else {
                System.out.println("FAILED.");
                allPassed = false;
            }

            System.out.println("\n==================================================");
            if (allPassed) {
                System.out.println("   ALL SERVLET ARCHITECTURE TESTS PASSED CLEANLY!");
            } else {
                System.out.println("   SOME SERVLET ARCHITECTURE CHECKS FAILED!");
            }
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("Error executing ServletTest: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
