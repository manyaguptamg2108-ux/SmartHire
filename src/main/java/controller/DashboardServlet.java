package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import model.enums.JobStatus;
import model.enums.UserRole;
import service.*;
import util.AuthUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller handling protected role-based dashboard routing and statistics compilation.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobSeekerService jobSeekerService = new JobSeekerService();
    private final RecruiterService recruiterService = new RecruiterService();
    private final CompanyService companyService = new CompanyService();
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private final InterviewService interviewService = new InterviewService();
    private final NotificationService notificationService = new NotificationService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserRole role = AuthUtil.getCurrentRole(request);
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = AuthUtil.getCurrentUserId(request);
        request.setAttribute("activePage", "dashboard");

        try {
            switch (role) {
                case JOB_SEEKER:
                    prepareJobSeekerDashboard(request, userId);
                    request.getRequestDispatcher("/WEB-INF/views/jobseeker/dashboard.jsp").forward(request, response);
                    break;
                case RECRUITER:
                    prepareRecruiterDashboard(request, userId);
                    request.getRequestDispatcher("/WEB-INF/views/recruiter/dashboard.jsp").forward(request, response);
                    break;
                case ADMIN:
                    prepareAdminDashboard(request);
                    request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/login");
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading dashboard metrics: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    private void prepareJobSeekerDashboard(HttpServletRequest request, int userId) throws Exception {
        request.setAttribute("pageTitle", "Job Seeker Dashboard");
        JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);
        request.setAttribute("jobSeeker", seeker);

        int profileCompletion = (seeker != null) ? jobSeekerService.calculateProfileCompletion(seeker) : 0;
        request.setAttribute("profileCompletion", profileCompletion);

        List<Job> activeJobs = jobService.searchJobs(null, null, null, JobStatus.ACTIVE);
        request.setAttribute("activeJobsCount", activeJobs.size());
        request.setAttribute("recentJobs", activeJobs.stream().limit(3).toList());

        List<Application> userApps = (seeker != null) ? applicationService.getApplicationsByJobSeeker(seeker.getId()) : new ArrayList<>();
        request.setAttribute("applicationsCount", userApps.size());
        request.setAttribute("recentApplications", userApps.stream().limit(3).toList());

        int interviewsCount = 0;
        if (seeker != null) {
            for (Application app : userApps) {
                List<Interview> appInterviews = interviewService.getInterviewsByApplication(app.getId());
                interviewsCount += appInterviews.size();
            }
        }
        request.setAttribute("interviewsCount", interviewsCount);

        List<Notification> unreadNotifs = notificationService.getUnreadNotifications(userId);
        request.setAttribute("unreadNotificationsCount", unreadNotifs.size());
        request.setAttribute("recentNotifications", notificationService.getUserNotifications(userId).stream().limit(3).toList());
    }

    private void prepareRecruiterDashboard(HttpServletRequest request, int userId) throws Exception {
        request.setAttribute("pageTitle", "Recruiter Dashboard");
        Recruiter recruiter = recruiterService.getByUserId(userId);
        request.setAttribute("recruiter", recruiter);

        Company company = null;
        List<Job> recruiterJobs = new ArrayList<>();
        int totalApplicants = 0;
        int totalInterviews = 0;
        List<Application> recentApps = new ArrayList<>();

        if (recruiter != null) {
            List<Company> list = companyService.getCompanyByRecruiterId(recruiter.getId());
            if (list != null && !list.isEmpty()) {
                company = list.get(0);
                recruiterJobs = jobService.getJobsByCompany(company.getId());
                for (Job job : recruiterJobs) {
                    List<Application> apps = applicationService.getApplicationsByJob(job.getId());
                    totalApplicants += apps.size();
                    recentApps.addAll(apps);

                    for (Application app : apps) {
                        totalInterviews += interviewService.getInterviewsByApplication(app.getId()).size();
                    }
                }
            }
        }

        request.setAttribute("company", company);
        request.setAttribute("activeJobsCount", recruiterJobs.size());
        request.setAttribute("totalApplicantsCount", totalApplicants);
        request.setAttribute("interviewsCount", totalInterviews);
        request.setAttribute("recentApplications", recentApps.stream().limit(5).toList());
    }

    private void prepareAdminDashboard(HttpServletRequest request) throws Exception {
        request.setAttribute("pageTitle", "Admin Control Center");
        List<User> allUsers = userService.getAllUsers();
        request.setAttribute("totalUsers", allUsers.size());
        request.setAttribute("totalJobSeekers", jobSeekerService.getAllProfiles().size());
        request.setAttribute("totalRecruiters", recruiterService.getAllRecruiters().size());
        request.setAttribute("totalCompanies", companyService.getAllCompanies().size());
        request.setAttribute("totalJobs", jobService.getAllJobs().size());
        request.setAttribute("totalApplications", applicationService.getAllApplications().size());
    }
}
