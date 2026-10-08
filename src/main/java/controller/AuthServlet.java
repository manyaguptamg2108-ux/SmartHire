package controller;

import exception.AuthenticationException;
import exception.DatabaseException;
import exception.InvalidUserException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.enums.UserRole;
import service.UserService;

import java.io.IOException;

/**
 * Controller handling user authentication, registration, and logout.
 */
@WebServlet(urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/login":
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                break;
            case "/register":
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if ("/login".equals(servletPath)) {
            handleLogin(request, response);
        } else if ("/register".equals(servletPath)) {
            handleRegister(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email != null) email = email.trim().toLowerCase();
        if (password != null) password = password.trim();

        try {
            User user = userService.loginUser(email, password);

            // Invalidate any existing session to prevent Session Fixation attacks
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create fresh new session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole().name());
            session.setAttribute("userName", user.getName());
            session.setAttribute("user", user);

            // Role-based redirection to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (AuthenticationException | DatabaseException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleStr = request.getParameter("role");

        if (name != null) name = name.trim();
        if (email != null) email = email.trim().toLowerCase();
        if (password != null) password = password.trim();
        if (confirmPassword != null) confirmPassword = confirmPassword.trim();

        try {
            UserRole role = null;
            if (roleStr != null && !roleStr.trim().isEmpty()) {
                try {
                    role = UserRole.valueOf(roleStr.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidUserException("Invalid user role selected.");
                }
            }

            if (role == UserRole.ADMIN) {
                throw new InvalidUserException("Registration as ADMIN is strictly prohibited.");
            }

            boolean registered = userService.registerUser(name, email, password, confirmPassword, role);
            if (registered) {
                User user = userService.findUserByEmail(email);
                if (user != null) {
                    // Invalidate old session to prevent Session Fixation attacks
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }

                    // Create fresh new session for newly registered user
                    HttpSession session = request.getSession(true);
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("userRole", user.getRole().name());
                    session.setAttribute("userName", user.getName());
                    session.setAttribute("user", user);

                    // Automatically redirect to role-specific dashboard
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
            }

            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("role", roleStr);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);

        } catch (InvalidUserException | DatabaseException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("role", roleStr);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
