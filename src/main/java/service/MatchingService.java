package service;

import model.Job;
import model.Skill;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Matching Service calculating skill overlap percentage between Job requirements and Job Seeker skills.
 */
public class MatchingService {

    /**
     * Calculates match percentage between job required skills and candidate skills.
     *
     * @param requiredSkills List of skills required for the job posting
     * @param candidateSkills List of skills possessed by the job seeker candidate
     * @return match percentage (0.0 to 100.0) rounded to 2 decimal places
     */
    public double calculateMatchPercentage(List<Skill> requiredSkills, List<Skill> candidateSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 100.00; // Default 100% match if job requires no specific skills
        }
        if (candidateSkills == null || candidateSkills.isEmpty()) {
            return 0.00; // 0% match if candidate has no listed skills
        }

        // Normalize required skills into a Set for case-insensitive deduplicated lookup
        Set<String> requiredSet = new HashSet<>();
        for (Skill s : requiredSkills) {
            if (s != null && s.getSkillName() != null && !s.getSkillName().trim().isEmpty()) {
                requiredSet.add(s.getSkillName().trim().toLowerCase());
            }
        }

        if (requiredSet.isEmpty()) {
            return 100.00;
        }

        // Normalize candidate skills
        Set<String> candidateSet = new HashSet<>();
        for (Skill s : candidateSkills) {
            if (s != null && s.getSkillName() != null && !s.getSkillName().trim().isEmpty()) {
                candidateSet.add(s.getSkillName().trim().toLowerCase());
            }
        }

        // Count overlap
        int matchedCount = 0;
        for (String req : requiredSet) {
            if (candidateSet.contains(req)) {
                matchedCount++;
            }
        }

        double rawPercentage = ((double) matchedCount / requiredSet.size()) * 100.0;
        return BigDecimal.valueOf(rawPercentage)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
