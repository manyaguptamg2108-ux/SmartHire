package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import model.enums.JobStatus;
import model.enums.JobType;
import service.CompanyService;
import service.JobService;
import service.RecruiterService;
import service.SkillService;
import util.AuthUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller handling job vacancy posting for recruiters.
 */
@WebServlet("/post-job")
public class PostJobServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobService jobService = new JobService();
    private final CompanyService companyService = new CompanyService();
    private final RecruiterService recruiterService = new RecruiterService();
    private final SkillService skillService = new SkillService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "post-job");
        request.setAttribute("pageTitle", "Post New Job Vacancy");

        int userId = AuthUtil.getCurrentUserId(request);

        try {
            Recruiter recruiter = recruiterService.getByUserId(userId);
            Company company = null;
            if (recruiter != null) {
                List<Company> list = companyService.getCompanyByRecruiterId(recruiter.getId());
                if (list != null && !list.isEmpty()) {
                    company = list.get(0);
                }
            }
            List<Skill> availableSkills = skillService.getAllSkills();

            request.setAttribute("company", company);
            request.setAttribute("availableSkills", availableSkills);

            request.getRequestDispatcher("/WEB-INF/views/recruiter/post-job.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error preparing post job form: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = AuthUtil.getCurrentUserId(request);

        try {
            Recruiter recruiter = recruiterService.getByUserId(userId);
            Company company = null;
            if (recruiter != null) {
                List<Company> list = companyService.getCompanyByRecruiterId(recruiter.getId());
                if (list != null && !list.isEmpty()) {
                    company = list.get(0);
                }
            }

            if (company == null) {
                request.getSession().setAttribute("postJobErrorMessage", "Please complete your Company Profile before posting job vacancies.");
                response.sendRedirect(request.getContextPath() + "/company-profile");
                return;
            }

            String title = request.getParameter("title");
            String description = request.getParameter("description");
            double salaryMin = Double.parseDouble(request.getParameter("salaryMin"));
            double salaryMax = Double.parseDouble(request.getParameter("salaryMax"));
            String location = request.getParameter("location");
            JobType jobType = JobType.valueOf(request.getParameter("jobType").toUpperCase());
            LocalDate deadline = LocalDate.parse(request.getParameter("applicationDeadline"));

            Job job = new Job(0, company.getId(), title, description, location, salaryMin, salaryMax, jobType, "2+ Years", "Bachelor's Degree", deadline, JobStatus.ACTIVE);

            String[] selectedSkillIds = request.getParameterValues("skills");
            List<Skill> requiredSkills = new ArrayList<>();
            if (selectedSkillIds != null) {
                for (String sId : selectedSkillIds) {
                    try {
                        int skillId = Integer.parseInt(sId);
                        Skill s = skillService.getSkillById(skillId);
                        if (s != null) requiredSkills.add(s);
                    } catch (Exception ignored) {}
                }
            }

            jobService.createJob(job, requiredSkills);

            request.getSession().setAttribute("myJobsSuccessMessage", "Job vacancy '" + title + "' posted successfully!");
            response.sendRedirect(request.getContextPath() + "/my-jobs");
        } catch (Exception e) {
            request.getSession().setAttribute("postJobErrorMessage", "Failed to post job vacancy: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/post-job");
        }
    }
}
