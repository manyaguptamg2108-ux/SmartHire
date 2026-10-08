package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.*;

import java.io.IOException;

/**
 * Admin Controller for system performance and usage reporting.
 */
@WebServlet("/admin/reports")
public class AdminReportsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService = new UserService();
    private final JobSeekerService jobSeekerService = new JobSeekerService();
    private final RecruiterService recruiterService = new RecruiterService();
    private final CompanyService companyService = new CompanyService();
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "admin-reports");
        request.setAttribute("pageTitle", "System Analytical Reports");

        try {
            request.setAttribute("userCount", userService.getAllUsers().size());
            request.setAttribute("jobSeekerCount", jobSeekerService.getAllProfiles().size());
            request.setAttribute("recruiterCount", recruiterService.getAllRecruiters().size());
            request.setAttribute("companyCount", companyService.getAllCompanies().size());
            request.setAttribute("jobCount", jobService.getAllJobs().size());
            request.setAttribute("applicationCount", applicationService.getAllApplications().size());

            request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error generating reports: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
