package service;

import model.Job;
import model.JobRecommendation;
import model.LiveJobResult;
import model.ResumeProfile;
import model.enums.JobStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core matching service comparing candidate ResumeProfile attributes against
 * local database active jobs and live external jobs.
 */
public class ResumeJobMatchingService {

    private final JobService jobService;
    private final LiveJobSearchService liveJobSearchService;

    public ResumeJobMatchingService() {
        this.jobService = new JobService();
        this.liveJobSearchService = new LiveJobSearchService();
    }

    public ResumeJobMatchingService(JobService jobService, LiveJobSearchService liveJobSearchService) {
        this.jobService = jobService;
        this.liveJobSearchService = liveJobSearchService;
    }

    /**
     * Matches a ResumeProfile against database jobs and live external jobs.
     *
     * @param profile Parsed candidate resume profile
     * @return Ranked list of JobRecommendation objects sorted by match percentage descending
     */
    public List<JobRecommendation> matchResumeToJobs(ResumeProfile profile) {
        List<JobRecommendation> recommendations = new ArrayList<>();
        if (profile == null) {
            return recommendations;
        }

        Set<String> candidateSkills = profile.getSkills() != null ? profile.getSkills() : Collections.emptySet();
        Set<String> candidateKeywords = profile.getExtractedKeywords() != null ? profile.getExtractedKeywords() : Collections.emptySet();

        // 1. Match against Local Database Jobs
        try {
            List<Job> allJobs = jobService.getAllJobs();
            if (allJobs != null) {
                for (Job job : allJobs) {
                    if (job != null && (job.getStatus() == null || job.getStatus() == JobStatus.ACTIVE)) {
                        JobRecommendation rec = calculateLocalJobMatch(profile, candidateSkills, candidateKeywords, job);
                        if (rec != null) {
                            recommendations.add(rec);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ResumeJobMatchingService] Error fetching local jobs: " + e.getMessage());
        }

        // 2. Match against Live External Jobs (if API is configured)
        try {
            if (liveJobSearchService != null && liveJobSearchService.isApiConfigured() && !candidateSkills.isEmpty()) {
                String searchKeyword = String.join(" ", candidateSkills.stream().limit(3).collect(Collectors.toList()));
                List<LiveJobResult> liveJobs = liveJobSearchService.searchLiveJobs(searchKeyword, "", "", 1);
                if (liveJobs != null) {
                    for (LiveJobResult liveJob : liveJobs) {
                        JobRecommendation rec = calculateLiveJobMatch(profile, candidateSkills, candidateKeywords, liveJob);
                        if (rec != null) {
                            recommendations.add(rec);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ResumeJobMatchingService] External live job search skipped: " + e.getMessage());
        }

        // 3. Sort by match percentage descending
        Collections.sort(recommendations);
        return recommendations;
    }

    private JobRecommendation calculateLocalJobMatch(ResumeProfile profile, Set<String> candidateSkills, Set<String> candidateKeywords, Job job) {
        JobRecommendation rec = new JobRecommendation();
        rec.setExternal(false);
        rec.setLocalJob(job);
        rec.setJobTitle(job.getTitle());
        rec.setCompanyName("SmartHire Employer");
        rec.setLocation(job.getLocation() != null ? job.getLocation() : "Remote");
        
        String salaryStr = "Negotiable";
        if (job.getSalaryMin() > 0 || job.getSalaryMax() > 0) {
            salaryStr = String.format("$%.0f - $%.0f", job.getSalaryMin(), job.getSalaryMax());
        }
        rec.setSalaryText(salaryStr);
        rec.setActionUrl("job-detail?id=" + job.getId());

        // Extract skills required by the job
        String reqText = (job.getEducationRequired() != null ? job.getEducationRequired() : "") + " " + (job.getExperienceRequired() != null ? job.getExperienceRequired() : "");
        List<String> requiredJobSkills = extractJobSkills(job.getTitle(), job.getDescription(), reqText);
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String reqSkill : requiredJobSkills) {
            if (containsSkillIgnoreCase(candidateSkills, reqSkill)) {
                matched.add(reqSkill);
            } else {
                missing.add(reqSkill);
            }
        }

        // Calculate score
        double matchScore = calculateMatchPercentage(candidateSkills, candidateKeywords, job.getTitle(), job.getDescription(), requiredJobSkills, matched.size());
        rec.setMatchPercentage(matchScore);
        rec.setMatchedSkills(matched);
        rec.setMissingSkills(missing);
        rec.setMatchExplanation(buildExplanation(matchScore, matched, missing));

        return rec;
    }

    private JobRecommendation calculateLiveJobMatch(ResumeProfile profile, Set<String> candidateSkills, Set<String> candidateKeywords, LiveJobResult liveJob) {
        JobRecommendation rec = new JobRecommendation();
        rec.setExternal(true);
        rec.setLiveJob(liveJob);
        rec.setJobTitle(liveJob.getTitle());
        rec.setCompanyName(liveJob.getCompanyName() != null ? liveJob.getCompanyName() : "External Company");
        rec.setLocation(liveJob.getLocation());
        
        String salaryStr = "Negotiable";
        if (liveJob.getSalaryMin() != null || liveJob.getSalaryMax() != null) {
            double min = liveJob.getSalaryMin() != null ? liveJob.getSalaryMin() : 0;
            double max = liveJob.getSalaryMax() != null ? liveJob.getSalaryMax() : 0;
            salaryStr = String.format("$%.0f - $%.0f", min, max);
        }
        rec.setSalaryText(salaryStr);
        rec.setActionUrl(liveJob.getRedirectUrl());

        List<String> requiredJobSkills = extractJobSkills(liveJob.getTitle(), liveJob.getDescription(), "");
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String reqSkill : requiredJobSkills) {
            if (containsSkillIgnoreCase(candidateSkills, reqSkill)) {
                matched.add(reqSkill);
            } else {
                missing.add(reqSkill);
            }
        }

        double matchScore = calculateMatchPercentage(candidateSkills, candidateKeywords, liveJob.getTitle(), liveJob.getDescription(), requiredJobSkills, matched.size());
        rec.setMatchPercentage(matchScore);
        rec.setMatchedSkills(matched);
        rec.setMissingSkills(missing);
        rec.setMatchExplanation(buildExplanation(matchScore, matched, missing));

        return rec;
    }

    private double calculateMatchPercentage(Set<String> candidateSkills, Set<String> candidateKeywords, String title, String description, List<String> requiredSkills, int matchedSkillCount) {
        double skillScore = 0.0;
        if (!requiredSkills.isEmpty()) {
            skillScore = ((double) matchedSkillCount / requiredSkills.size()) * 70.0;
        } else if (!candidateSkills.isEmpty()) {
            skillScore = 40.0;
        }

        double textBonus = 0.0;
        String combinedText = (title + " " + (description != null ? description : "")).toLowerCase();
        for (String skill : candidateSkills) {
            if (combinedText.contains(skill.toLowerCase())) {
                textBonus += 5.0;
            }
        }
        for (String kw : candidateKeywords) {
            if (combinedText.contains(kw.toLowerCase())) {
                textBonus += 2.0;
            }
        }

        double totalScore = Math.min(100.0, Math.max(15.0, skillScore + textBonus));
        return Math.round(totalScore * 10.0) / 10.0;
    }

    private List<String> extractJobSkills(String title, String description, String requirements) {
        String fullText = (title + " " + (description != null ? description : "") + " " + (requirements != null ? requirements : "")).toLowerCase();
        String[] skillDict = {
            "Java", "Python", "C++", "C#", "JavaScript", "TypeScript", "React", "Angular", "Vue.js", "Node.js",
            "Express", "Spring", "Spring Boot", "Hibernate", "SQL", "MySQL", "MariaDB", "PostgreSQL", "MongoDB",
            "Oracle", "HTML", "CSS", "Git", "GitHub", "Docker", "Kubernetes", "AWS", "Azure", "GCP", "Linux",
            "REST", "RESTful", "GraphQL", "Microservices", "Agile", "Scrum", "Jenkins", "Maven", "Gradle",
            "Data Analysis", "Machine Learning", "System Design", "OOP", "JDBC", "JSP", "Servlet"
        };

        Set<String> found = new LinkedHashSet<>();
        for (String s : skillDict) {
            if (fullText.contains(s.toLowerCase())) {
                found.add(s);
            }
        }
        return new ArrayList<>(found);
    }

    private boolean containsSkillIgnoreCase(Set<String> skills, String target) {
        for (String s : skills) {
            if (s.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private String buildExplanation(double matchPercentage, List<String> matched, List<String> missing) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Match Score: %.1f%%. ", matchPercentage));
        if (!matched.isEmpty()) {
            sb.append("Matched Skills: ").append(String.join(", ", matched)).append(". ");
        } else {
            sb.append("No direct skill keyword matches found. ");
        }
        if (!missing.isEmpty()) {
            sb.append("Skills to consider: ").append(String.join(", ", missing)).append(".");
        }
        return sb.toString();
    }
}
