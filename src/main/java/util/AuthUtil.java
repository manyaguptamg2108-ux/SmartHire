package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.enums.UserRole;

/**
 * Reusable Session & Authorization Utility for SmartHire Servlets and Filters.
 */
public class AuthUtil {

    private AuthUtil() {}

    public static boolean isLoggedIn(HttpServletRequest request) {
        if (request == null) return false;
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("userId") != null;
    }

    public static Integer getCurrentUserId(HttpServletRequest request) {
        if (!isLoggedIn(request)) return null;
        HttpSession session = request.getSession(false);
        Object id = session.getAttribute("userId");
        return (id instanceof Integer) ? (Integer) id : null;
    }

    public static UserRole getCurrentRole(HttpServletRequest request) {
        if (!isLoggedIn(request)) return null;
        HttpSession session = request.getSession(false);
        Object roleObj = session.getAttribute("userRole");
        if (roleObj != null) {
            try {
                return UserRole.valueOf(roleObj.toString());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public static String getCurrentUserName(HttpServletRequest request) {
        if (!isLoggedIn(request)) return null;
        HttpSession session = request.getSession(false);
        Object name = session.getAttribute("userName");
        return (name != null) ? name.toString() : null;
    }

    public static boolean hasRole(HttpServletRequest request, UserRole expectedRole) {
        UserRole current = getCurrentRole(request);
        return current != null && current == expectedRole;
    }
}
