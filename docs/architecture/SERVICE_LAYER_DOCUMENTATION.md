# SmartHire Service Layer & Business Logic Documentation

## 1. Service Layer Architecture
The Service Layer acts as the intermediary business engine between Controller Servlets and Data Access Objects (DAOs).

```
Controller / Servlet
        ↓
  Service Layer
        ↓
    DAO Layer
        ↓
  MariaDB / MySQL
```

### Key Architectural Guidelines:
- **Zero Raw SQL**: Service classes never execute raw SQL strings or create `PreparedStatement` instances.
- **Pure Persistence Delegation**: All database reading and writing operations are delegated exclusively to DAO interfaces (`UserDAOInterface`, `JobDAOInterface`, etc.).
- **Business Rule Enforcement**: Input validation, profile completion scoring, application eligibility, workflow status rules, and skill matching are encapsulated inside service methods.

---

## 2. Service Component Catalog

### 2.1. `UserService`
- **Responsibilities**: Manages user registration, profile updates, status activation/deactivation.
- **Business Rules**:
  - Validates name and email format.
  - Rejects duplicate emails before database persistence.
  - Defaults status to `ACTIVE`.

### 2.2. `JobSeekerService`
- **Responsibilities**: Manages candidate profiles and dynamic profile completion calculation.
- **Business Rules**:
  - Profile completion is computed dynamically from profile fields: Phone (+15%), Education (+20%), Experience (+20%), Location (+15%), Bio (+15%), Resume (+15%), capped at 100%.

### 2.3. `RecruiterService`
- **Responsibilities**: Manages recruiter profile data and corporate designation details.

### 2.4. `CompanyService`
- **Responsibilities**: Manages employer organizational profiles linked to recruiters.

### 2.5. `SkillService`
- **Responsibilities**: Catalog management for technical skills with automatic name normalization.
- **Business Rules**:
  - Trims and normalizes skill names (`" Java "` -> `"Java"`).
  - Preserves deduplication rules from `Skill.equals()` / `hashCode()`.

### 2.6. `JobService`
- **Responsibilities**: Manages job postings, deadlines, status states, and skill requirements.
- **Business Rules**:
  - Title and description are mandatory.
  - `salaryMin` and `salaryMax` must be non-negative.
  - `salaryMax` cannot be less than `salaryMin`.
  - Application deadline must be present and cannot be in the past for new jobs.
  - `EXPIRED` and `CLOSED` jobs are prevented from receiving applications.

### 2.7. `ApplicationService`
- **Responsibilities**: Application submission eligibility validation and status workflow transition enforcement.
- **Business Rules**:
  - Validates that target job is `ACTIVE` and deadline has not passed.
  - Rejects duplicate submissions from the same job seeker.
  - Enforces Application Status Workflow Transitions.

### 2.8. `InterviewService`
- **Responsibilities**: Schedules and validates candidate interview rounds.
- **Business Rules**:
  - Validates application existence.
  - `ONLINE` interviews require a non-empty `meetingLink`.
  - `OFFLINE` interviews require a non-empty `location`.
  - Automatically updates application status to `INTERVIEW`.

### 2.9. `NotificationService`
- **Responsibilities**: Dispatches and manages user in-app notifications.

### 2.10. `MatchingService`
- **Responsibilities**: Skill overlap percentage calculation between job requirements and candidate profile.
- **Formula**:
  $$\text{Match Percentage} = \frac{\text{matched required skills}}{\text{total required skills}} \times 100$$
- **Business Rules**:
  - Case-insensitive skill matching.
  - Returns 100.0% if a job specifies no required skills.
  - Returns 0.0% if candidate has no listed skills.

---

## 3. Application Status Workflow Transition Rules

```
APPLIED
   ↓
UNDER_REVIEW
   ↓
SHORTLISTED
   ↓
INTERVIEW
   ↓
SELECTED
```

- **Rejection / Withdrawal**: Applications at any active stage (`APPLIED`, `UNDER_REVIEW`, `SHORTLISTED`, `INTERVIEW`) can transition to `REJECTED` or `WITHDRAWN`.
- **Terminal States**: `SELECTED`, `REJECTED`, and `WITHDRAWN` are locked terminal states. Backward transitions (e.g., `SELECTED` -> `APPLIED`) are strictly rejected.
