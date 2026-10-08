# STEP 2 Status Report - SmartHire Database Implementation

## 1. Database Creation Result
- **Database Name**: `smarthire`
- **Execution Result**: Success (0 errors)
- **Character Set**: `utf8mb4`
- **Engine**: `InnoDB`

## 2. Number of Tables Created
Exactly **10 Tables**:
1. `users`
2. `job_seekers`
3. `recruiters`
4. `companies`
5. `skills`
6. `jobs`
7. `job_skills`
8. `applications`
9. `interviews`
10. `notifications`

## 3. Foreign Key Implementation & Behavior Result
- `job_seekers.user_id` -> `users(id)` (`ON DELETE RESTRICT`)
- `recruiters.user_id` -> `users(id)` (`ON DELETE RESTRICT`)
- `companies.recruiter_id` -> `recruiters(id)` (`ON DELETE RESTRICT`)
- `jobs.company_id` -> `companies(id)` (`ON DELETE RESTRICT`)
- `job_skills.job_id` -> `jobs(id)` (`ON DELETE CASCADE`)
- `job_skills.skill_id` -> `skills(id)` (`ON DELETE RESTRICT`)
- `applications.job_id` -> `jobs(id)` (`ON DELETE RESTRICT`)
- `applications.job_seeker_id` -> `job_seekers(id)` (`ON DELETE RESTRICT`)
- `interviews.application_id` -> `applications(id)` (`ON DELETE RESTRICT`)
- `notifications.user_id` -> `users(id)` (`ON DELETE CASCADE`)

## 4. Constraint Verification Results (Tested & Empirical Verification)

| Constraint Test | SQL Executed | Expected Output | Actual Output | Status |
|---|---|---|---|---|
| **1. Duplicate User Email** | `INSERT INTO users (name, email, password_hash, role) VALUES ('Dup User', 'admin@smarthire.com', 'hash', 'JOB_SEEKER');` | `ERROR 1062 (23000)` | `ERROR 1062 (23000): Duplicate entry 'admin@smarthire.com' for key 'users.email'` | **PASSED** (Rejected) |
| **2. Duplicate Skill** | `INSERT INTO skills (skill_name) VALUES ('Java');` | `ERROR 1062 (23000)` | `ERROR 1062 (23000): Duplicate entry 'Java' for key 'skills.skill_name'` | **PASSED** (Rejected) |
| **3. Duplicate Job Application** | `INSERT INTO applications (job_id, job_seeker_id) VALUES (1, 1);` | `ERROR 1062 (23000)` | `ERROR 1062 (23000): Duplicate entry '1-1' for key 'applications.uq_job_seeker_application'` | **PASSED** (Rejected) |
| **4. Invalid Foreign Key** | `INSERT INTO job_seekers (user_id) VALUES (9999);` | `ERROR 1452 (23000)` | `ERROR 1452 (23000): Cannot add or update a child row: a foreign key constraint fails ...` | **PASSED** (Rejected) |
| **5. Match Percentage > 100** | `INSERT INTO applications (job_id, job_seeker_id, match_percentage) VALUES (2, 2, 105.00);` | `ERROR 3819 (HY000)` | `ERROR 3819 (HY000): Check constraint 'chk_match_percentage' is violated.` | **PASSED** (Rejected) |
| **6. Negative Match Percentage** | `INSERT INTO applications (job_id, job_seeker_id, match_percentage) VALUES (2, 2, -10.00);` | `ERROR 3819 (HY000)` | `ERROR 3819 (HY000): Check constraint 'chk_match_percentage' is violated.` | **PASSED** (Rejected) |
| **7. Profile Completion > 100** | `UPDATE job_seekers SET profile_completion = 150 WHERE id = 1;` | `ERROR 3819 (HY000)` | `ERROR 3819 (HY000): Check constraint 'chk_profile_completion' is violated.` | **PASSED** (Rejected) |
| **8. Negative Salary** | `INSERT INTO jobs (company_id, title, description, salary_min, job_type, application_deadline) VALUES (1, 'Test', 'Desc', -500.00, 'FULL_TIME', '2026-12-31');` | `ERROR 3819 (HY000)` | `ERROR 3819 (HY000): Check constraint 'chk_salary_min' is violated.` | **PASSED** (Rejected) |

## 5. Sample Data Insertion Result
- **Users Created**: 4 (`admin@smarthire.com`, `recruiter@techcorp.com`, `john.doe@email.com`, `jane.smith@email.com`)
- **Demo Password**: `Password123` (SHA-256: `ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f`)
- **Recruiter Profiles**: 1
- **Job Seeker Profiles**: 2
- **Companies**: 1 (`TechCorp Solutions`)
- **Skills**: 10 (`Java`, `C++`, `Python`, `SQL`, `HTML`, `CSS`, `JavaScript`, `Spring Boot`, `Git`, `REST API`)
- **Jobs Posted**: 3 (`Java Developer`, `Python Developer`, `Frontend Developer`)
- **Job Skills Mapped**: 14 entries in `job_skills`
- **Applications**: 1
- **Interviews**: 1
- **Notifications**: 2

## 6. SQL Errors
- **0 errors** during schema creation and sample data population.

## 7. Exact Commands Used for Verification
```bash
# 1. Start test instance and execute script
mysqld.exe --initialize-insecure --datadir="<scratch_path>"
mysqld.exe --datadir="<scratch_path>" --port=3307
mysql.exe -u root --host=127.0.0.1 --port=3307 < database.sql

# 2. Database & Table Verification
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "SHOW DATABASES; USE smarthire; SHOW TABLES;"
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; DESCRIBE users; DESCRIBE job_seekers; DESCRIBE recruiters; DESCRIBE companies; DESCRIBE skills; DESCRIBE jobs; DESCRIBE job_skills; DESCRIBE applications; DESCRIBE interviews; DESCRIBE notifications;"

# 3. Constraint Testing Queries
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; INSERT INTO users (name, email, password_hash, role) VALUES ('Dup User', 'admin@smarthire.com', 'hash', 'JOB_SEEKER');"
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; INSERT INTO skills (skill_name) VALUES ('Java');"
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; INSERT INTO applications (job_id, job_seeker_id) VALUES (1, 1);"
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; INSERT INTO job_seekers (user_id) VALUES (9999);"
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; INSERT INTO applications (job_id, job_seeker_id, match_percentage) VALUES (2, 2, 105.00);"
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; INSERT INTO applications (job_id, job_seeker_id, match_percentage) VALUES (2, 2, -10.00);"
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; UPDATE job_seekers SET profile_completion = 150 WHERE id = 1;"
mysql.exe -u root --host=127.0.0.1 --port=3307 -e "USE smarthire; INSERT INTO jobs (company_id, title, description, salary_min, job_type, application_deadline) VALUES (1, 'Test', 'Desc', -500.00, 'FULL_TIME', '2026-12-31');"
```
