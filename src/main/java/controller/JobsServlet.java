package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Job;
import model.LiveJobResult;
import model.enums.JobStatus;
import model.enums.JobType;
import service.JobService;
import service.LiveJobSearchService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Controller for browsing local SmartHire database jobs and real live online external job listings.
 */
@WebServlet("/jobs")
public class JobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobService jobService = new JobService();
    private final LiveJobSearchService liveJobSearchService = new LiveJobSearchService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("activePage", "jobs");
        request.setAttribute("pageTitle", "Browse & Live Search Jobs");

        // Read query parameters
        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            keyword = request.getParameter("query");
        }
        String location = request.getParameter("location");
        String jobTypeStr = request.getParameter("jobType");
        String source = request.getParameter("source"); // "all", "local", "live"
        if (source == null || source.trim().isEmpty()) {
            source = "all";
        }

        String pageParam = request.getParameter("page");
        int page = 1;
        if (pageParam != null) {
            try { page = Math.max(1, Integer.parseInt(pageParam)); } catch (Exception ignored) {}
        }

        JobType jobType = null;
        if (jobTypeStr != null && !jobTypeStr.trim().isEmpty()) {
            try {
                jobType = JobType.valueOf(jobTypeStr.toUpperCase());
            } catch (Exception ignored) {}
        }

        try {
            // [1] Fetch Local SmartHire Database Jobs
            List<Job> localJobs = Collections.emptyList();
            if (!"live".equalsIgnoreCase(source)) {
                localJobs = jobService.searchJobs(keyword, location, jobType, JobStatus.ACTIVE);
            }
            request.setAttribute("jobs", localJobs);

            // [2] Live External API Job Search Handling
            boolean apiConfigured = liveJobSearchService.isApiConfigured();
            request.setAttribute("isLiveApiConfigured", apiConfigured);

            List<LiveJobResult> liveJobs = Collections.emptyList();
            String liveSearchStatus = null;

            if (!"local".equalsIgnoreCase(source)) {
                if (apiConfigured) {
                    try {
                        liveJobs = liveJobSearchService.searchLiveJobs(keyword, location, jobTypeStr, page);
                        if (liveJobs.isEmpty() && (keyword != null || location != null)) {
                            liveSearchStatus = "No live online jobs found for '" +
                                    (keyword != null ? keyword : "") +
                                    (location != null ? " " + location : "") + "'.";
                        }
                    } catch (IllegalStateException e) {
                        liveSearchStatus = e.getMessage();
                    } catch (Exception e) {
                        liveSearchStatus = "Live job search is temporarily unavailable. (" + e.getMessage() + ")";
                    }
                } else {
                    liveSearchStatus = "Live job search API is not configured. Set ADZUNA_APP_ID & ADZUNA_APP_KEY environment variables to enable real online listings.";
                }
            }

            request.setAttribute("liveJobs", liveJobs);
            request.setAttribute("liveSearchStatus", liveSearchStatus);
            request.setAttribute("keyword", keyword);
            request.setAttribute("location", location);
            request.setAttribute("jobType", jobTypeStr);
            request.setAttribute("source", source);
            request.setAttribute("currentPage", page);

            request.getRequestDispatcher("/WEB-INF/views/jobseeker/jobs.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error retrieving jobs: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
