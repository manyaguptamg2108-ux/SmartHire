package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Application;
import service.ApplicationService;

import java.io.IOException;
import java.util.List;

/**
 * Admin Controller for inspecting all platform applications.
 */
@WebServlet("/admin/applications")
public class AdminApplicationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "admin-applications");
        request.setAttribute("pageTitle", "Platform Applications Management");

        try {
            List<Application> applications = applicationService.getAllApplications();
            request.setAttribute("applications", applications);
            request.getRequestDispatcher("/WEB-INF/views/admin/applications.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching applications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
