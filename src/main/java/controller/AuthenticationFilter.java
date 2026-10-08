package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.enums.UserRole;
import util.AuthUtil;

import java.io.IOException;

/**
 * Filter protecting authenticated routes against unauthorized access and enforcing role-based permissions.
 */
@WebFilter(urlPatterns = {
    "/dashboard", "/profile", "/jobs", "/job-detail", "/applications",
    "/interviews", "/notifications", "/settings", "/company-profile",
    "/post-job", "/my-jobs", "/applicants", "/admin/*"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Prevent response caching for protected pages
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        // 1. Authentication check
        if (!AuthUtil.isLoggedIn(httpRequest)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        UserRole role = AuthUtil.getCurrentRole(httpRequest);
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // 2. Admin routes authorization
        if (path.startsWith("/admin") && role != UserRole.ADMIN) {
            httpRequest.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(httpRequest, httpResponse);
            return;
        }

        // 3. Recruiter routes authorization
        if ((path.startsWith("/post-job") || path.startsWith("/my-jobs") || path.startsWith("/applicants") || path.startsWith("/company-profile"))
                && role == UserRole.JOB_SEEKER) {
            httpRequest.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(httpRequest, httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }
}
