package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.UserService;

import java.io.IOException;
import java.util.List;

/**
 * Admin Controller for user account management and activation/deactivation.
 */
@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "admin-users");
        request.setAttribute("pageTitle", "User Accounts Administration");

        try {
            List<User> users = userService.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error retrieving users: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParam = request.getParameter("userId");
        String action = request.getParameter("action");

        try {
            int userId = Integer.parseInt(userIdParam);
            if ("activate".equals(action)) {
                userService.activateUser(userId);
                request.getSession().setAttribute("adminSuccessMessage", "User #" + userId + " activated successfully.");
            } else if ("deactivate".equals(action)) {
                userService.deactivateUser(userId);
                request.getSession().setAttribute("adminSuccessMessage", "User #" + userId + " deactivated successfully.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("adminErrorMessage", "Failed to update user status: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
