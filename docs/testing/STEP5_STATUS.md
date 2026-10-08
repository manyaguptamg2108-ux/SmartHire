# STEP 5 Status Report - SmartHire Service Layer & Business Logic

## 1. Service Classes Created (`src/main/java/service/`)
- `UserService.java` (User registration, duplicate email rejection, account activation/deactivation)
- `JobSeekerService.java` (Candidate profile management & dynamic completion calculation)
- `RecruiterService.java` (Recruiter profile validation & management)
- `CompanyService.java` (Employer organization management & recruiter association rules)
- `SkillService.java` (Skill catalog management & case-insensitive name normalization)
- `JobService.java` (Job posting lifecycle, salary range validation, deadline checks, skill coordination)
- `ApplicationService.java` (Application submission eligibility, duplicate check, workflow status transition enforcement)
- `InterviewService.java` (Candidate interview scheduling, online link / offline location validation, auto status update)
- `NotificationService.java` (Synchronous in-app notification dispatch and unread management)
- `MatchingService.java` (Skill overlap percentage calculation with deduplicated Set comparison)

## 2. Business Rules Implemented
1. **User Validation**: Mandates non-empty name, valid email format, and unique email constraint.
2. **Job Seeker Profile Completion**: Computes actual percentage score dynamically (Phone +15%, Education +20%, Experience +20%, Location +15%, Bio +15%, Resume +15%).
3. **Skill Normalization**: Trims and standardizes skill names (`" Java "` -> `"Java"`).
4. **Job Posting Rules**: Rejects negative salaries, `salaryMax < salaryMin`, missing companies, missing deadlines, or past deadlines on creation.
5. **Job-Skill Association**: Coordinates junction mapping (`job_skills`) at the service layer via `JobSkillsDAO`.
6. **Application Eligibility**: Ensures job exists, job status is `ACTIVE`, deadline has not passed, candidate exists, and candidate has not already applied.
7. **Application Workflow Status Transitions**: Enforces valid state transitions (`APPLIED` -> `UNDER_REVIEW` -> `SHORTLISTED` -> `INTERVIEW` -> `SELECTED`). Prevents illegal backward jumps or edits from terminal states (`SELECTED`, `REJECTED`, `WITHDRAWN`).
8. **Interview Scheduling Validation**: Enforces mandatory meeting links for `ONLINE` interviews and location addresses for `OFFLINE` interviews. Auto-updates application status to `INTERVIEW`.
9. **Matching Calculation**: Calculates exact skill match percentage using set intersection math:
   $$\text{Match Percentage} = \frac{\text{matched required skills}}{\text{total required skills}} \times 100$$

## 3. Test Results (`util.ServiceTest`)
- **Execution Command**: `java -cp "target/classes;<classpath>" util.ServiceTest`
- **Output**: 100% Passed across all 12 test sections:
  - `[1] User Service Lookup` -> **PASSED**
  - `[2] Profile Completion Calculation` -> **PASSED** (100% computed)
  - `[3] Recruiter Lookup` -> **PASSED**
  - `[4] Company Lookup` -> **PASSED**
  - `[5] Skill Lookup & Normalization` -> **PASSED**
  - `[6] Job Search & Retrieval` -> **PASSED**
  - `[7] Job Validation (Past deadline rejection)` -> **PASSED**
  - `[8] Application Eligibility & Duplicate Rejection` -> **PASSED**
  - `[9] Application Workflow Status Transitions` -> **PASSED**
  - `[10] Interview Scheduling Validation` -> **PASSED**
  - `[11] Notification Operations` -> **PASSED**
  - `[12] Skill Match Percentage Calculation` -> **PASSED** (66.67% computed)

## 4. Backward Compatibility Verification Results
- **STEP 4 JDBCTest**: **PASSED** (`java -cp "target/classes;<classpath>" util.JDBCTest`)
- **STEP 4 DAOTest**: **PASSED** (`java -cp "target/classes;<classpath>" util.DAOTest`)
- **STEP 3 OOPTest**: **PASSED** (`java -cp target/classes util.OOPTest`)

## 5. Build & Packaging Result
- **Command**: `mvn package`
- **Result**: `BUILD SUCCESS` (0 compilation or packaging errors)
- **Artifact**: `target/SmartHire.war`

## 6. Remaining Issues / Problems
- None.
