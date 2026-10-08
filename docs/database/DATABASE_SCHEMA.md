# SmartHire Database Schema & Architecture Documentation

## 1. Database Specifications
- **Database Name**: `smarthire`
- **Supported RDBMS**: MariaDB 10.x / 12.x, MySQL 8.0+
- **Character Set**: `utf8mb4`
- **Storage Engine**: `InnoDB`

---

## 2. Entity Relationship Diagram (ERD)

```
users
 ├── job_seekers
 └── recruiters
       └── companies
             └── jobs
                  └── job_skills
                       └── skills

job_seekers
      └── applications
             └── interviews

users
      └── notifications
```

---

## 3. Table Definitions & Constraints

### 3.1. `users`
Core identity table for system access control.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`name`**: `VARCHAR(100) NOT NULL`
- **`email`**: `VARCHAR(150) NOT NULL UNIQUE`
- **`password_hash`**: `VARCHAR(255) NOT NULL` (SHA-256 encrypted)
- **`role`**: `VARCHAR(20) NOT NULL` — `CHECK (role IN ('JOB_SEEKER', 'RECRUITER', 'ADMIN'))`
- **`status`**: `VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'` — `CHECK (status IN ('ACTIVE', 'INACTIVE'))`
- **`created_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
- **`updated_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`

### 3.2. `job_seekers`
Extended profile for candidate users.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`user_id`**: `INT NOT NULL UNIQUE` (FK -> `users.id` `ON DELETE RESTRICT`)
- **`phone`**: `VARCHAR(20)`
- **`education`**: `TEXT`
- **`experience`**: `TEXT`
- **`location`**: `VARCHAR(150)`
- **`bio`**: `TEXT`
- **`resume_path`**: `VARCHAR(255)`
- **`profile_completion`**: `INT DEFAULT 0` — `CHECK (profile_completion BETWEEN 0 AND 100)`

### 3.3. `recruiters`
Extended profile for recruiter users.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`user_id`**: `INT NOT NULL UNIQUE` (FK -> `users.id` `ON DELETE RESTRICT`)
- **`phone`**: `VARCHAR(20)`
- **`designation`**: `VARCHAR(100)`

### 3.4. `companies`
Organization profiles managed by recruiters.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`recruiter_id`**: `INT NOT NULL` (FK -> `recruiters.id` `ON DELETE RESTRICT`)
- **`company_name`**: `VARCHAR(150) NOT NULL`
- **`description`**: `TEXT`
- **`location`**: `VARCHAR(150)`
- **`website`**: `VARCHAR(255)`
- **`industry`**: `VARCHAR(100)`
- **`company_size`**: `VARCHAR(50)`
- **`created_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`

### 3.5. `skills`
Master catalog of technical and soft skills.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`skill_name`**: `VARCHAR(100) NOT NULL UNIQUE`

### 3.6. `jobs`
Job postings created by companies.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`company_id`**: `INT NOT NULL` (FK -> `companies.id` `ON DELETE RESTRICT`)
- **`title`**: `VARCHAR(150) NOT NULL`
- **`description`**: `TEXT NOT NULL`
- **`location`**: `VARCHAR(150)`
- **`salary_min`**: `DECIMAL(12,2) DEFAULT 0.00` — `CHECK (salary_min >= 0)`
- **`salary_max`**: `DECIMAL(12,2) DEFAULT 0.00` — `CHECK (salary_max >= 0)`
- **`job_type`**: `VARCHAR(20) NOT NULL` — `CHECK (job_type IN ('FULL_TIME', 'PART_TIME', 'INTERNSHIP', 'REMOTE'))`
- **`experience_required`**: `VARCHAR(50)`
- **`education_required`**: `VARCHAR(100)`
- **`application_deadline`**: `DATE NOT NULL`
- **`status`**: `VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'` — `CHECK (status IN ('ACTIVE', 'EXPIRED', 'CLOSED'))`
- **`created_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
- **`updated_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`

### 3.7. `job_skills`
Junction table mapping skills required for specific job postings.
- **`job_id`**: `INT NOT NULL` (FK -> `jobs.id` `ON DELETE CASCADE`)
- **`skill_id`**: `INT NOT NULL` (FK -> `skills.id` `ON DELETE RESTRICT`)
- **Primary Key**: `(job_id, skill_id)`

### 3.8. `applications`
Job seeker applications submitted to job postings.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`job_id`**: `INT NOT NULL` (FK -> `jobs.id` `ON DELETE RESTRICT`)
- **`job_seeker_id`**: `INT NOT NULL` (FK -> `job_seekers.id` `ON DELETE RESTRICT`)
- **`status`**: `VARCHAR(20) NOT NULL DEFAULT 'APPLIED'` — `CHECK (status IN ('APPLIED', 'UNDER_REVIEW', 'SHORTLISTED', 'INTERVIEW', 'SELECTED', 'REJECTED', 'WITHDRAWN'))`
- **`match_percentage`**: `DECIMAL(5,2) DEFAULT 0.00` — `CHECK (match_percentage BETWEEN 0 AND 100)`
- **`cover_letter`**: `TEXT`
- **`applied_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
- **`updated_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`
- **Unique Constraint**: `UNIQUE (job_id, job_seeker_id)`

### 3.9. `interviews`
Scheduled interviews linked to job applications.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`application_id`**: `INT NOT NULL` (FK -> `applications.id` `ON DELETE RESTRICT`)
- **`interview_date`**: `DATE NOT NULL`
- **`interview_time`**: `TIME NOT NULL`
- **`interview_type`**: `VARCHAR(20) NOT NULL` — `CHECK (interview_type IN ('ONLINE', 'OFFLINE'))`
- **`meeting_link`**: `VARCHAR(255)`
- **`location`**: `VARCHAR(255)`
- **`notes`**: `TEXT`
- **`status`**: `VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED'` — `CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED'))`
- **`created_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`

### 3.10. `notifications`
In-app alerts dispatched to system users.
- **`id`**: `INT AUTO_INCREMENT PRIMARY KEY`
- **`user_id`**: `INT NOT NULL` (FK -> `users.id` `ON DELETE CASCADE`)
- **`title`**: `VARCHAR(150) NOT NULL`
- **`message`**: `TEXT NOT NULL`
- **`notification_type`**: `VARCHAR(30) NOT NULL` — `CHECK (notification_type IN ('NEW_APPLICATION', 'APPLICATION_STATUS', 'INTERVIEW', 'JOB_EXPIRY', 'GENERAL'))`
- **`is_read`**: `BOOLEAN NOT NULL DEFAULT FALSE`
- **`created_at`**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`

---

## 4. Foreign Key & ON DELETE Behavior Rationale

| Child Table | Foreign Key Column | Parent Table | Action `ON DELETE` | Rationale |
|---|---|---|---|---|
| `job_seekers` | `user_id` | `users(id)` | `RESTRICT` | Prevents orphan profiles and accidental account deletion without audit. |
| `recruiters` | `user_id` | `users(id)` | `RESTRICT` | Protects corporate user links. |
| `companies` | `recruiter_id` | `recruiters(id)` | `RESTRICT` | Preserves company profiles when recruiter records change. |
| `jobs` | `company_id` | `companies(id)` | `RESTRICT` | Prevents accidental deletion of companies with active job postings. |
| `job_skills` | `job_id` | `jobs(id)` | `CASCADE` | Junction table: deleting a job cleans up its skill tags. |
| `job_skills` | `skill_id` | `skills(id)` | `RESTRICT` | Prevents removing master skill definitions attached to posted jobs. |
| `applications` | `job_id` | `jobs(id)` | `RESTRICT` | Protects applicant submission history even if a job closes. |
| `applications` | `job_seeker_id` | `job_seekers(id)` | `RESTRICT` | Protects historical application records. |
| `interviews` | `application_id` | `applications(id)` | `RESTRICT` | Maintains interview audit logs. |
| `notifications` | `user_id` | `users(id)` | `CASCADE` | User deletion safely clears personal inbox notifications. |

---

## 5. Performance Indexes
- `idx_users_email` (`users.email`)
- `idx_users_role` (`users.role`)
- `idx_jobs_title` (`jobs.title`)
- `idx_jobs_location` (`jobs.location`)
- `idx_jobs_status` (`jobs.status`)
- `idx_jobs_deadline` (`jobs.application_deadline`)
- `idx_applications_job_id` (`applications.job_id`)
- `idx_applications_job_seeker_id` (`applications.job_seeker_id`)
- `idx_applications_status` (`applications.status`)
- `idx_notifications_user_id` (`notifications.user_id`)
- `idx_notifications_is_read` (`notifications.is_read`)

---

## 6. Seed / Demo Accounts

> **Common Demo Password**: `Password123`  
> **SHA-256 Hash**: `ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f`

| Role | Email | Demo Password | SHA-256 Hash |
|---|---|---|---|
| `ADMIN` | `admin@smarthire.com` | `Password123` | `ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f` |
| `RECRUITER` | `recruiter@techcorp.com` | `Password123` | `ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f` |
| `JOB_SEEKER` | `john.doe@email.com` | `Password123` | `ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f` |
| `JOB_SEEKER` | `jane.smith@email.com` | `Password123` | `ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f` |
