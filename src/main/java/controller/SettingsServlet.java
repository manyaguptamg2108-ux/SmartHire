package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.UserService;
import util.AuthUtil;
import util.PasswordUtil;

import java.io.IOException;

/**
 * Controller for account settings and password updates.
 */
@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "settings");
        request.setAttribute("pageTitle", "Account Settings");

        int userId = AuthUtil.getCurrentUserId(request);
        try {
            User user = userService.findUserById(userId);
            request.setAttribute("userAccount", user);
            request.getRequestDispatcher("/WEB-INF/views/jobseeker/settings.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading settings: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = AuthUtil.getCurrentUserId(request);
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");

        try {
            User user = userService.findUserById(userId);

            if (!PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
                request.getSession().setAttribute("settingsErrorMessage", "Current password is incorrect.");
                response.sendRedirect(request.getContextPath() + "/settings");
                return;
            }

            if (newPassword == null || newPassword.trim().length() < 6) {
                request.getSession().setAttribute("settingsErrorMessage", "New password must be at least 6 characters long.");
                response.sendRedirect(request.getContextPath() + "/settings");
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                request.getSession().setAttribute("settingsErrorMessage", "New passwords do not match.");
                response.sendRedirect(request.getContextPath() + "/settings");
                return;
            }

            String newHash = PasswordUtil.hashPassword(newPassword);
            user.setPasswordHash(newHash);
            userService.updateUser(user);

            request.getSession().setAttribute("settingsSuccessMessage", "Password updated successfully!");
            response.sendRedirect(request.getContextPath() + "/settings");
        } catch (Exception e) {
            request.getSession().setAttribute("settingsErrorMessage", "Failed to update settings: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/settings");
        }
    }
}
