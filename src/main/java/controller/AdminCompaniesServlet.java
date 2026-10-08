package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Company;
import service.CompanyService;

import java.io.IOException;
import java.util.List;

/**
 * Admin Controller for company management.
 */
@WebServlet("/admin/companies")
public class AdminCompaniesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CompanyService companyService = new CompanyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "admin-companies");
        request.setAttribute("pageTitle", "Registered Companies Registry");

        try {
            List<Company> companies = companyService.getAllCompanies();
            request.setAttribute("companies", companies);
            request.getRequestDispatcher("/WEB-INF/views/admin/companies.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error retrieving companies: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
