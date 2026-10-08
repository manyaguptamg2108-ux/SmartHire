package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.AuthUtil;

import java.io.IOException;

/**
 * Root / Home controller dispatching unauthenticated requests to landing page
 * and authenticated requests to role-based dashboard.
 */
@WebServlet(urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        if (AuthUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.getRequestDispatcher("/WEB-INF/views/landing.jsp").forward(request, response);
        }
    }
}
