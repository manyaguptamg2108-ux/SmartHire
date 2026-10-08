package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Admin Controller for system global settings.
 */
@WebServlet("/admin/settings")
public class AdminSettingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "admin-settings");
        request.setAttribute("pageTitle", "System Administrative Settings");
        request.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(request, response);
    }
}
