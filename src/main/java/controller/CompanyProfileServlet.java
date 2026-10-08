package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Company;
import model.Recruiter;
import model.User;
import model.enums.UserStatus;
import service.CompanyService;
import service.RecruiterService;
import service.UserService;
import util.AuthUtil;

import java.io.IOException;
import java.util.List;

/**
 * Controller managing Company Profile for recruiters.
 */
@WebServlet("/company-profile")
public class CompanyProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CompanyService companyService = new CompanyService();
    private final RecruiterService recruiterService = new RecruiterService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "company-profile");
        request.setAttribute("pageTitle", "Company Profile");

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

            request.setAttribute("recruiter", recruiter);
            request.setAttribute("company", company);

            request.getRequestDispatcher("/WEB-INF/views/recruiter/company-profile.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading company profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = AuthUtil.getCurrentUserId(request);

        try {
            User user = userService.findUserById(userId);
            Recruiter recruiter = recruiterService.getByUserId(userId);
            if (recruiter == null) {
                recruiter = new Recruiter(userId, user != null ? user.getName() : "", user != null ? user.getEmail() : "", "", UserStatus.ACTIVE, "", "Senior Recruiter");
                recruiterService.createRecruiterProfile(recruiter);
            }

            String companyName = request.getParameter("companyName");
            String industry = request.getParameter("industry");
            String location = request.getParameter("location");
            String description = request.getParameter("description");
            String website = request.getParameter("website");

            List<Company> list = companyService.getCompanyByRecruiterId(recruiter.getId());
            Company company = (list != null && !list.isEmpty()) ? list.get(0) : null;

            if (company == null) {
                company = new Company(0, recruiter.getId(), companyName, description, location, website, industry, "10-50");
                companyService.createCompany(company);
            } else {
                company.setCompanyName(companyName);
                company.setIndustry(industry);
                company.setLocation(location);
                company.setDescription(description);
                company.setWebsite(website);
                companyService.updateCompany(company);
            }

            request.getSession().setAttribute("companySuccessMessage", "Company profile updated successfully!");
            response.sendRedirect(request.getContextPath() + "/company-profile");
        } catch (Exception e) {
            request.getSession().setAttribute("companyErrorMessage", "Failed to save company profile: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/company-profile");
        }
    }
}
