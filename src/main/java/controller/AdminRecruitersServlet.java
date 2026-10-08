package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Recruiter;
import service.RecruiterService;

import java.io.IOException;
import java.util.List;

/**
 * Admin Controller for inspecting recruiters.
 */
@WebServlet("/admin/recruiters")
public class AdminRecruitersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final RecruiterService recruiterService = new RecruiterService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "admin-recruiters");
        request.setAttribute("pageTitle", "Recruiters Registry");

        try {
            List<Recruiter> recruiters = recruiterService.getAllRecruiters();
            request.setAttribute("recruiters", recruiters);
            request.getRequestDispatcher("/WEB-INF/views/admin/recruiters.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching recruiters: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
