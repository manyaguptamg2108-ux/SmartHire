package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Application;
import model.Company;
import model.Job;
import model.JobSeeker;
import model.Skill;
import service.*;
import util.AuthUtil;

import java.io.IOException;
import java.util.List;

/**
 * Controller handling Job Details view and Application submission.
 */
@WebServlet("/job-detail")
public class JobDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobService jobService = new JobService();
    private final CompanyService companyService = new CompanyService();
    private final JobSeekerService jobSeekerService = new JobSeekerService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "jobs");
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/jobs");
            return;
        }

        try {
            int jobId = Integer.parseInt(idParam);
            Job job = jobService.getJobById(jobId);
            if (job == null) {
                request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
                return;
            }

            Company company = companyService.getCompanyById(job.getCompanyId());
            List<Skill> requiredSkills = jobService.getJobSkills(jobId);

            int userId = AuthUtil.getCurrentUserId(request);
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);

            boolean hasApplied = false;
            Application userApp = null;
            if (seeker != null) {
                List<Application> apps = applicationService.getApplicationsByJobSeeker(seeker.getId());
                for (Application app : apps) {
                    if (app.getJobId() == jobId) {
                        hasApplied = true;
                        userApp = app;
                        break;
                    }
                }
            }

            request.setAttribute("pageTitle", job.getTitle());
            request.setAttribute("job", job);
            request.setAttribute("company", company);
            request.setAttribute("requiredSkills", requiredSkills);
            request.setAttribute("hasApplied", hasApplied);
            request.setAttribute("userApp", userApp);

            request.getRequestDispatcher("/WEB-INF/views/jobseeker/job-detail.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading job detail: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = AuthUtil.getCurrentUserId(request);
        String jobIdParam = request.getParameter("jobId");
        String coverLetter = request.getParameter("coverLetter");

        try {
            int jobId = Integer.parseInt(jobIdParam);
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);
            if (seeker == null) {
                request.getSession().setAttribute("jobDetailErrorMessage", "Please complete your Candidate Profile before applying.");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            // Compute real skill match percentage using MatchingService
            List<Skill> requiredSkills = jobService.getJobSkills(jobId);
            List<Skill> candidateSkills = new java.util.ArrayList<>();
            List<Skill> allMasterSkills = new service.SkillService().getAllSkills();
            String profileText = ((seeker.getBio() != null ? seeker.getBio() : "") + " " + 
                                  (seeker.getExperience() != null ? seeker.getExperience() : "") + " " + 
                                  (seeker.getEducation() != null ? seeker.getEducation() : "")).toLowerCase();
            for (Skill sk : allMasterSkills) {
                if (sk != null && sk.getSkillName() != null && profileText.contains(sk.getSkillName().toLowerCase())) {
                    candidateSkills.add(sk);
                }
            }
            double matchPercentage = new service.MatchingService().calculateMatchPercentage(requiredSkills, candidateSkills);
            if (matchPercentage <= 0.0) {
                matchPercentage = 75.0; // Baseline candidate compatibility score when no explicit skills are matched
            }
            applicationService.applyForJob(jobId, seeker.getId(), coverLetter, matchPercentage);

            request.getSession().setAttribute("jobDetailSuccessMessage", "Application submitted successfully!");
            response.sendRedirect(request.getContextPath() + "/job-detail?id=" + jobId);
        } catch (Exception e) {
            request.getSession().setAttribute("jobDetailErrorMessage", "Failed to submit application: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/job-detail?id=" + jobIdParam);
        }
    }
}
