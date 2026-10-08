package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Application;
import model.Job;
import model.JobSeeker;
import service.ApplicationService;
import service.JobSeekerService;
import service.JobService;
import util.AuthUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller managing Job Applications for Job Seekers.
 */
@WebServlet("/applications")
public class ApplicationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ApplicationService applicationService = new ApplicationService();
    private final JobSeekerService jobSeekerService = new JobSeekerService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "applications");
        request.setAttribute("pageTitle", "My Submitted Applications");

        int userId = AuthUtil.getCurrentUserId(request);

        try {
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);
            List<Application> applications = (seeker != null) 
                    ? applicationService.getApplicationsByJobSeeker(seeker.getId())
                    : List.of();

            Map<Integer, Job> jobMap = new HashMap<>();
            for (Application app : applications) {
                Job job = jobService.getJobById(app.getJobId());
                if (job != null) {
                    jobMap.put(app.getJobId(), job);
                }
            }

            request.setAttribute("applications", applications);
            request.setAttribute("jobMap", jobMap);

            request.getRequestDispatcher("/WEB-INF/views/jobseeker/applications.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching applications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String appIdParam = request.getParameter("applicationId");
        try {
            int appId = Integer.parseInt(appIdParam);
            applicationService.withdrawApplication(appId);
            request.getSession().setAttribute("appSuccessMessage", "Application #" + appId + " has been withdrawn.");
        } catch (Exception e) {
            request.getSession().setAttribute("appErrorMessage", "Failed to withdraw application: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/applications");
    }
}
