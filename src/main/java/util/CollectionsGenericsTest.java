package util;

import dao.*;
import model.*;
import model.enums.JobStatus;
import model.enums.JobType;
import thread.ThreadSafeApplicationTracker;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Automated Test Suite for Step 10: Collections Framework, Generics & Type Safety verification.
 */
public class CollectionsGenericsTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SMART HIRE - STEP 10 COLLECTIONS & GENERICS TEST ");
        System.out.println("==================================================");

        boolean allPassed = true;

        try {
            // [1] Test GenericDAO Type Safety
            System.out.println("\n[1] Testing GenericDAO Type Safety & Implementation...");
            GenericDAO<User> userDAO = new UserDAO();
            GenericDAO<Job> jobDAO = new JobDAO();
            GenericDAO<Skill> skillDAO = new SkillDAO();

            if (userDAO instanceof GenericDAO && jobDAO instanceof GenericDAO && skillDAO instanceof GenericDAO) {
                System.out.println(" -> GenericDAO<T> interface implemented by all DAOs. PASSED.");
            } else {
                System.out.println(" -> GenericDAO type verification failed. FAILED.");
                allPassed = false;
            }

            // [2] Test List<T> Collection Operations, Streams & Comparators
            System.out.println("\n[2] Testing List<Job> Stream Filtering, Mapping & Sorting...");
            List<Job> jobList = new ArrayList<>();
            jobList.add(new Job(1, 1, "Java Developer", "Description 1", "Remote", 90000, 120000, JobType.FULL_TIME, "3 yrs", "BS", LocalDate.now().plusDays(10), JobStatus.ACTIVE));
            jobList.add(new Job(2, 1, "Frontend Engineer", "Description 2", "New York", 85000, 110000, JobType.PART_TIME, "2 yrs", "BS", LocalDate.now().plusDays(5), JobStatus.EXPIRED));
            jobList.add(new Job(3, 2, "Backend Architect", "Description 3", "Remote", 130000, 160000, JobType.FULL_TIME, "5 yrs", "MS", LocalDate.now().plusDays(15), JobStatus.ACTIVE));

            // Stream filtering for ACTIVE jobs
            List<Job> activeJobs = jobList.stream()
                    .filter(j -> j.getStatus() == JobStatus.ACTIVE)
                    .collect(Collectors.toList());

            // Sorting by SalaryMax descending using Comparator
            List<Job> sortedBySalary = jobList.stream()
                    .sorted(Comparator.comparing(Job::getSalaryMax).reversed())
                    .collect(Collectors.toList());

            System.out.println(" -> Total Jobs in List: " + jobList.size());
            System.out.println(" -> Filtered Active Jobs count: " + activeJobs.size() + " (Expected: 2)");
            System.out.println(" -> Top Salary Job: " + sortedBySalary.get(0).getTitle() + " ($ " + sortedBySalary.get(0).getSalaryMax() + ")");

            if (activeJobs.size() == 2 && sortedBySalary.get(0).getId() == 3) {
                System.out.println(" -> List<T> stream filtering & sorting. PASSED.");
            } else {
                System.out.println(" -> List stream processing failed. FAILED.");
                allPassed = false;
            }

            // [3] Test Set<Skill> Deduplication & Normalized Skill equals/hashCode
            System.out.println("\n[3] Testing Set<Skill> Deduplication & Normalized equals/hashCode...");
            Set<Skill> skillSet = new HashSet<>();
            skillSet.add(new Skill(1, "Java"));
            skillSet.add(new Skill(2, "java"));
            skillSet.add(new Skill(3, " Java "));
            skillSet.add(new Skill(4, "Python"));
            skillSet.add(new Skill(5, "PYTHON"));

            System.out.println(" -> Added 5 variations of 'Java' and 'Python' into HashSet.");
            System.out.println(" -> Set size after deduplication: " + skillSet.size() + " (Expected: 2)");

            if (skillSet.size() == 2) {
                System.out.println(" -> Set<Skill> deduplication verified. PASSED.");
            } else {
                System.out.println(" -> Set deduplication failed. FAILED.");
                allPassed = false;
            }

            // [4] Test Map<String, Object> & Map<Integer, Job> Statistics Behavior
            System.out.println("\n[4] Testing Map<K,V> Lookup & Typed Metrics...");
            Map<Integer, Job> jobMap = new HashMap<>();
            for (Job j : jobList) {
                jobMap.put(j.getId(), j);
            }

            Map<String, Object> dashboardStats = new HashMap<>();
            dashboardStats.put("totalJobs", jobList.size());
            dashboardStats.put("activeJobs", activeJobs.size());
            dashboardStats.put("topJobTitle", sortedBySalary.get(0).getTitle());

            System.out.println(" -> JobMap lookup ID 1: " + jobMap.get(1).getTitle());
            System.out.println(" -> Dashboard Stats Map: " + dashboardStats);

            if (jobMap.get(1) != null && dashboardStats.get("activeJobs").equals(2)) {
                System.out.println(" -> Map<K,V> statistics & entity lookup. PASSED.");
            } else {
                System.out.println(" -> Map statistics failed. FAILED.");
                allPassed = false;
            }

            // [5] Test ConcurrentHashMap Concurrency Consistency
            System.out.println("\n[5] Testing ConcurrentHashMap<Integer, AtomicInteger> Consistency...");
            ThreadSafeApplicationTracker tracker = new ThreadSafeApplicationTracker();
            tracker.recordJobApplication(101);
            tracker.recordJobApplication(101);
            tracker.recordJobApplication(102);

            ConcurrentHashMap<Integer, AtomicInteger> jobCounts = tracker.getJobApplicationCounts();
            System.out.println(" -> Job #101 applications: " + tracker.getJobApplicationCount(101) + " (Expected: 2)");
            System.out.println(" -> Job #102 applications: " + tracker.getJobApplicationCount(102) + " (Expected: 1)");

            if (tracker.getJobApplicationCount(101) == 2 && tracker.getJobApplicationCount(102) == 1) {
                System.out.println(" -> ConcurrentHashMap thread-safe tracking. PASSED.");
            } else {
                System.out.println(" -> ConcurrentHashMap tracking failed. FAILED.");
                allPassed = false;
            }

            System.out.println("\n==================================================");
            if (allPassed) {
                System.out.println("   ALL STEP 10 COLLECTIONS & GENERICS TESTS PASSED!");
            } else {
                System.out.println("   SOME COLLECTIONS & GENERICS TESTS FAILED.");
                System.exit(1);
            }
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("COLLECTIONS & GENERICS TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
