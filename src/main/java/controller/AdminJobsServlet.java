package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Job;
import service.JobService;

import java.io.IOException;
import java.util.List;

/**
 * Admin Controller for inspecting all platform job listings.
 */
@WebServlet("/admin/jobs")
public class AdminJobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "admin-jobs");
        request.setAttribute("pageTitle", "Platform Job Listings Management");

        try {
            List<Job> jobs = jobService.getAllJobs();
            request.setAttribute("jobs", jobs);
            request.getRequestDispatcher("/WEB-INF/views/admin/jobs.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching jobs: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
