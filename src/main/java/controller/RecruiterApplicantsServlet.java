package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Application;
import model.Company;
import model.Job;
import model.Recruiter;
import model.enums.ApplicationStatus;
import service.ApplicationService;
import service.CompanyService;
import service.JobService;
import service.RecruiterService;
import util.AuthUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for viewing candidates and updating application statuses.
 */
@WebServlet("/applicants")
public class RecruiterApplicantsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();
    private final RecruiterService recruiterService = new RecruiterService();
    private final CompanyService companyService = new CompanyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "applicants");
        request.setAttribute("pageTitle", "Candidate Applications");

        int userId = AuthUtil.getCurrentUserId(request);
        String jobIdParam = request.getParameter("jobId");

        try {
            Recruiter recruiter = recruiterService.getByUserId(userId);
            List<Application> applications = new ArrayList<>();

            if (recruiter != null) {
                List<Company> list = companyService.getCompanyByRecruiterId(recruiter.getId());
                if (list != null && !list.isEmpty()) {
                    Company company = list.get(0);
                    if (jobIdParam != null && !jobIdParam.trim().isEmpty()) {
                        int jobId = Integer.parseInt(jobIdParam);
                        applications = applicationService.getApplicationsByJob(jobId);
                    } else {
                        List<Job> recruiterJobs = jobService.getJobsByCompany(company.getId());
                        for (Job job : recruiterJobs) {
                            applications.addAll(applicationService.getApplicationsByJob(job.getId()));
                        }
                    }
                }
            }

            request.setAttribute("applications", applications);
            request.setAttribute("selectedJobId", jobIdParam);
            request.getRequestDispatcher("/WEB-INF/views/recruiter/applicants.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading applicants: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String appIdParam = request.getParameter("applicationId");
        String newStatusStr = request.getParameter("status");

        try {
            int appId = Integer.parseInt(appIdParam);
            ApplicationStatus newStatus = ApplicationStatus.valueOf(newStatusStr.toUpperCase());
            applicationService.updateApplicationStatus(appId, newStatus);
            request.getSession().setAttribute("applicantsSuccessMessage", "Application #" + appId + " status updated to " + newStatus);
        } catch (Exception e) {
            request.getSession().setAttribute("applicantsErrorMessage", "Failed to update status: " + e.getMessage());
        }

        String redirectUrl = request.getContextPath() + "/applicants";
        String jobIdParam = request.getParameter("jobId");
        if (jobIdParam != null && !jobIdParam.trim().isEmpty()) {
            redirectUrl += "?jobId=" + jobIdParam;
        }
        response.sendRedirect(redirectUrl);
    }
}
