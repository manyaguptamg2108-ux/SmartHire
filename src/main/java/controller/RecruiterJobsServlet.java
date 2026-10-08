package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Company;
import model.Job;
import model.Recruiter;
import service.CompanyService;
import service.JobService;
import service.RecruiterService;
import util.AuthUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing recruiter's posted vacancies.
 */
@WebServlet("/my-jobs")
public class RecruiterJobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobService jobService = new JobService();
    private final RecruiterService recruiterService = new RecruiterService();
    private final CompanyService companyService = new CompanyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "my-jobs");
        request.setAttribute("pageTitle", "My Posted Job Vacancies");

        int userId = AuthUtil.getCurrentUserId(request);

        try {
            Recruiter recruiter = recruiterService.getByUserId(userId);
            List<Job> jobs = new ArrayList<>();
            if (recruiter != null) {
                List<Company> list = companyService.getCompanyByRecruiterId(recruiter.getId());
                if (list != null && !list.isEmpty()) {
                    Company company = list.get(0);
                    jobs = jobService.getJobsByCompany(company.getId());
                }
            }

            request.setAttribute("jobs", jobs);
            request.getRequestDispatcher("/WEB-INF/views/recruiter/my-jobs.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching posted jobs: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String jobIdParam = request.getParameter("jobId");

        try {
            int jobId = Integer.parseInt(jobIdParam);
            if ("close".equals(action)) {
                jobService.closeJob(jobId);
                request.getSession().setAttribute("myJobsSuccessMessage", "Job #" + jobId + " closed successfully.");
            } else if ("delete".equals(action)) {
                jobService.deleteJob(jobId);
                request.getSession().setAttribute("myJobsSuccessMessage", "Job #" + jobId + " deleted successfully.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("myJobsErrorMessage", "Action failed: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/my-jobs");
    }
}
