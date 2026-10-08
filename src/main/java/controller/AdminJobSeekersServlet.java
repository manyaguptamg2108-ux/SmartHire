package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.JobSeeker;
import service.JobSeekerService;

import java.io.IOException;
import java.util.List;

/**
 * Admin Controller for inspecting job seekers.
 */
@WebServlet("/admin/job-seekers")
public class AdminJobSeekersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobSeekerService jobSeekerService = new JobSeekerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "admin-jobseekers");
        request.setAttribute("pageTitle", "Job Seekers Registry");

        try {
            List<JobSeeker> seekers = jobSeekerService.getAllProfiles();
            request.setAttribute("seekers", seekers);
            request.getRequestDispatcher("/WEB-INF/views/admin/job-seekers.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching job seekers: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
