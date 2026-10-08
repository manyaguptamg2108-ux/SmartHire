package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.JobRecommendation;
import model.JobSeeker;
import model.ResumeProfile;
import service.JobSeekerService;
import service.ResumeJobMatchingService;
import service.ResumeParserService;
import util.AuthUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controller handling Resume Upload, rule-based Parsing, Smart Recommendation generation,
 * and candidate profile update.
 */
@WebServlet("/resume-match")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 5,       // 5MB
    maxRequestSize = 1024 * 1024 * 10    // 10MB
)
public class ResumeMatchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ResumeParserService resumeParserService = new ResumeParserService();
    private final ResumeJobMatchingService resumeJobMatchingService = new ResumeJobMatchingService();
    private final JobSeekerService jobSeekerService = new JobSeekerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "resume-match");
        request.setAttribute("pageTitle", "Smart Resume Match & Job Recommendations");

        // Keep any existing session profile or recommendations if available
        HttpSession session = request.getSession(false);
        if (session != null) {
            ResumeProfile sessionProfile = (ResumeProfile) session.getAttribute("resumeProfile");
            @SuppressWarnings("unchecked")
            List<JobRecommendation> sessionRecs = (List<JobRecommendation>) session.getAttribute("recommendations");
            if (sessionProfile != null) {
                request.setAttribute("resumeProfile", sessionProfile);
            }
            if (sessionRecs != null) {
                request.setAttribute("recommendations", sessionRecs);
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/jobseeker/resume-match.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "resume-match");
        request.setAttribute("pageTitle", "Smart Resume Match & Job Recommendations");

        String action = request.getParameter("action");
        if (action == null) {
            action = "upload";
        }

        HttpSession session = request.getSession(true);

        if ("updateProfile".equalsIgnoreCase(action)) {
            handleUpdateProfile(request, response, session);
        } else {
            handleResumeUpload(request, response, session);
        }
    }

    private void handleResumeUpload(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        try {
            Part filePart = request.getPart("resumeFile");
            if (filePart == null || filePart.getSize() <= 0) {
                request.setAttribute("errorMessage", "Please select a valid PDF or DOCX resume file to upload.");
                request.getRequestDispatcher("/WEB-INF/views/jobseeker/resume-match.jsp").forward(request, response);
                return;
            }

            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String lowerName = fileName.toLowerCase();
            if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".docx")) {
                request.setAttribute("errorMessage", "Unsupported file format. Please upload a PDF (.pdf) or Word document (.docx).");
                request.getRequestDispatcher("/WEB-INF/views/jobseeker/resume-match.jsp").forward(request, response);
                return;
            }

            if (filePart.getSize() > 5 * 1024 * 1024) {
                request.setAttribute("errorMessage", "File size exceeds 5MB limit. Please upload a smaller resume file.");
                request.getRequestDispatcher("/WEB-INF/views/jobseeker/resume-match.jsp").forward(request, response);
                return;
            }

            // Extract resume text & structured profile
            ResumeProfile profile;
            try (InputStream is = filePart.getInputStream()) {
                profile = resumeParserService.parseResume(is, fileName);
            }

            // Match against database jobs and live external jobs
            List<JobRecommendation> recommendations = resumeJobMatchingService.matchResumeToJobs(profile);

            // Store in request and session
            session.setAttribute("resumeProfile", profile);
            session.setAttribute("recommendations", recommendations);
            request.setAttribute("resumeProfile", profile);
            request.setAttribute("recommendations", recommendations);
            request.setAttribute("successMessage", "Resume analyzed successfully! Found " + recommendations.size() + " job recommendations based on your extracted skills.");

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error processing resume: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/jobseeker/resume-match.jsp").forward(request, response);
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        Integer userId = AuthUtil.getCurrentUserId(request);
        ResumeProfile profile = (ResumeProfile) session.getAttribute("resumeProfile");

        if (profile == null) {
            request.setAttribute("errorMessage", "No active resume profile found to sync. Please upload a resume first.");
            request.getRequestDispatcher("/WEB-INF/views/jobseeker/resume-match.jsp").forward(request, response);
            return;
        }

        if (userId == null) {
            request.setAttribute("errorMessage", "You must be logged in as a Job Seeker to update your profile.");
            request.getRequestDispatcher("/WEB-INF/views/jobseeker/resume-match.jsp").forward(request, response);
            return;
        }

        try {
            JobSeeker seeker = jobSeekerService.getProfileByUserId(userId);
            if (seeker == null) {
                seeker = new JobSeeker();
                seeker.setId(userId);
            }

            if (!profile.getSkills().isEmpty()) {
                seeker.setBio("Skills: " + String.join(", ", profile.getSkills()) + " | " + (profile.getSummary() != null ? profile.getSummary() : ""));
            }
            if (!profile.getEducation().isEmpty()) {
                seeker.setEducation(String.join("; ", profile.getEducation()));
            }
            if (!profile.getExperience().isEmpty()) {
                seeker.setExperience(String.join("; ", profile.getExperience()));
            }

            boolean updated = jobSeekerService.updateProfile(seeker);
            if (updated) {
                request.setAttribute("successMessage", "Candidate profile updated successfully with extracted resume skills & experience!");
            } else {
                request.setAttribute("errorMessage", "Could not update profile. Please check database configuration.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to update candidate profile: " + e.getMessage());
        }

        request.setAttribute("resumeProfile", profile);
        @SuppressWarnings("unchecked")
        List<JobRecommendation> recs = (List<JobRecommendation>) session.getAttribute("recommendations");
        request.setAttribute("recommendations", recs);

        request.getRequestDispatcher("/WEB-INF/views/jobseeker/resume-match.jsp").forward(request, response);
    }
}
