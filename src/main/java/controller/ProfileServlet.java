package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.JobSeeker;
import model.User;
import model.enums.UserStatus;
import service.JobSeekerService;
import service.UserService;
import util.AuthUtil;

import java.io.IOException;

/**
 * Controller handling Job Seeker Profile management.
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobSeekerService jobSeekerService = new JobSeekerService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = AuthUtil.getCurrentUserId(request);
        request.setAttribute("activePage", "profile");
        request.setAttribute("pageTitle", "My Candidate Profile");

        try {
            User user = userService.findUserById(userId);
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);
            if (seeker == null) {
                seeker = new JobSeeker(userId, user != null ? user.getName() : "", user != null ? user.getEmail() : "", "", UserStatus.ACTIVE, "", "", "", "", "", "", 0);
            }

            int profileCompletion = jobSeekerService.calculateProfileCompletion(seeker);
            request.setAttribute("userAccount", user);
            request.setAttribute("jobSeeker", seeker);
            request.setAttribute("profileCompletion", profileCompletion);

            request.getRequestDispatcher("/WEB-INF/views/jobseeker/profile.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = AuthUtil.getCurrentUserId(request);

        try {
            User user = userService.findUserById(userId);
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);
            
            String phone = request.getParameter("phone");
            String education = request.getParameter("education");
            String experience = request.getParameter("experience");
            String location = request.getParameter("location");
            String bio = request.getParameter("bio");
            String resumePath = request.getParameter("resumePath");

            if (seeker == null) {
                seeker = new JobSeeker(userId, user.getName(), user.getEmail(), "", UserStatus.ACTIVE, phone, education, experience, location, bio, resumePath, 50);
                jobSeekerService.createProfile(seeker);
            } else {
                seeker.setPhone(phone);
                seeker.setEducation(education);
                seeker.setExperience(experience);
                seeker.setLocation(location);
                seeker.setBio(bio);
                seeker.setResumePath(resumePath);
                jobSeekerService.updateProfile(seeker);
            }

            request.getSession().setAttribute("profileSuccessMessage", "Profile updated successfully!");
            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            doGet(request, response);
        }
    }
}
