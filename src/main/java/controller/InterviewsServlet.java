package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import model.enums.UserRole;
import service.*;
import util.AuthUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing scheduled interviews for candidates and recruiters.
 */
@WebServlet("/interviews")
public class InterviewsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final InterviewService interviewService = new InterviewService();
    private final ApplicationService applicationService = new ApplicationService();
    private final JobSeekerService jobSeekerService = new JobSeekerService();
    private final RecruiterService recruiterService = new RecruiterService();
    private final CompanyService companyService = new CompanyService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "interviews");
        request.setAttribute("pageTitle", "Scheduled Interviews");

        UserRole role = AuthUtil.getCurrentRole(request);
        int userId = AuthUtil.getCurrentUserId(request);
        List<Interview> interviews = new ArrayList<>();

        try {
            if (role == UserRole.JOB_SEEKER) {
                JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);
                if (seeker != null) {
                    List<Application> apps = applicationService.getApplicationsByJobSeeker(seeker.getId());
                    for (Application app : apps) {
                        interviews.addAll(interviewService.getInterviewsByApplication(app.getId()));
                    }
                }
            } else if (role == UserRole.RECRUITER || role == UserRole.ADMIN) {
                Recruiter recruiter = recruiterService.getByUserId(userId);
                if (recruiter != null) {
                    List<Company> list = companyService.getCompanyByRecruiterId(recruiter.getId());
                    if (list != null && !list.isEmpty()) {
                        Company company = list.get(0);
                        List<Job> jobs = jobService.getJobsByCompany(company.getId());
                        for (Job j : jobs) {
                            List<Application> apps = applicationService.getApplicationsByJob(j.getId());
                            for (Application app : apps) {
                                interviews.addAll(interviewService.getInterviewsByApplication(app.getId()));
                            }
                        }
                    }
                } else if (role == UserRole.ADMIN) {
                    interviews = interviewService.getAllInterviews();
                }
            }

            request.setAttribute("interviews", interviews);
            request.getRequestDispatcher("/WEB-INF/views/jobseeker/interviews.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching interviews: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
