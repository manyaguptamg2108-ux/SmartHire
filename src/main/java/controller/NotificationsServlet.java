package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Notification;
import service.NotificationService;
import util.AuthUtil;

import java.io.IOException;
import java.util.List;

/**
 * Controller for user notification center and status management.
 */
@WebServlet("/notifications")
public class NotificationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "notifications");
        request.setAttribute("pageTitle", "Notification Center");

        int userId = AuthUtil.getCurrentUserId(request);

        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            request.setAttribute("notifications", notifications);
            request.getRequestDispatcher("/WEB-INF/views/jobseeker/notifications.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching notifications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String notifIdParam = request.getParameter("notificationId");
        try {
            int notifId = Integer.parseInt(notifIdParam);
            notificationService.markAsRead(notifId);
        } catch (Exception ignored) {}

        response.sendRedirect(request.getContextPath() + "/notifications");
    }
}
