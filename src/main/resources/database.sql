-- ==================================================
-- SmartHire Database Schema Implementation
-- Target RDBMS: MariaDB 10.x/12.x & MySQL 8.0+
-- ==================================================

CREATE DATABASE IF NOT EXISTS smarthire;
USE smarthire;

-- Disable Foreign Key checks temporarily for clean setup
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS interviews;
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS job_skills;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS companies;
DROP TABLE IF EXISTS recruiters;
DROP TABLE IF EXISTS job_seekers;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- ==================================================
-- 1. USERS TABLE
-- Foreign Key Behavior: Core parent entity.
-- ==================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('JOB_SEEKER', 'RECRUITER', 'ADMIN')),
    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 2. JOB_SEEKERS TABLE
-- Foreign Key Behavior: ON DELETE RESTRICT on user_id to prevent accidental user deletion without cleaning profile.
-- ==================================================
CREATE TABLE job_seekers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    phone VARCHAR(20),
    education TEXT,
    experience TEXT,
    location VARCHAR(150),
    bio TEXT,
    resume_path VARCHAR(255),
    profile_completion INT DEFAULT 0,
    CONSTRAINT fk_job_seeker_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_profile_completion CHECK (profile_completion BETWEEN 0 AND 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 3. RECRUITERS TABLE
-- Foreign Key Behavior: ON DELETE RESTRICT on user_id.
-- ==================================================
CREATE TABLE recruiters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    phone VARCHAR(20),
    designation VARCHAR(100),
    CONSTRAINT fk_recruiter_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 4. COMPANIES TABLE
-- Foreign Key Behavior: ON DELETE RESTRICT on recruiter_id to preserve corporate record.
-- ==================================================
CREATE TABLE companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id INT NOT NULL,
    company_name VARCHAR(150) NOT NULL,
    description TEXT,
    location VARCHAR(150),
    website VARCHAR(255),
    industry VARCHAR(100),
    company_size VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_recruiter FOREIGN KEY (recruiter_id) REFERENCES recruiters(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 5. SKILLS TABLE
-- Foreign Key Behavior: Master skill repository.
-- ==================================================
CREATE TABLE skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    skill_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 6. JOBS TABLE
-- Foreign Key Behavior: ON DELETE RESTRICT on company_id to prevent accidental deletion of companies with active jobs.
-- ==================================================
CREATE TABLE jobs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(150),
    salary_min DECIMAL(12,2) DEFAULT 0.00,
    salary_max DECIMAL(12,2) DEFAULT 0.00,
    job_type VARCHAR(20) NOT NULL,
    experience_required VARCHAR(50),
    education_required VARCHAR(100),
    application_deadline DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    CONSTRAINT chk_salary_min CHECK (salary_min >= 0),
    CONSTRAINT chk_salary_max CHECK (salary_max >= 0),
    CONSTRAINT chk_job_type CHECK (job_type IN ('FULL_TIME', 'PART_TIME', 'INTERNSHIP', 'REMOTE')),
    CONSTRAINT chk_job_status CHECK (status IN ('ACTIVE', 'EXPIRED', 'CLOSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 7. JOB_SKILLS TABLE (Many-to-Many Relationship)
-- Foreign Key Behavior: ON DELETE CASCADE on job_id (deleting a job cleans skill associations); ON DELETE RESTRICT on skill_id.
-- ==================================================
CREATE TABLE job_skills (
    job_id INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (job_id, skill_id),
    CONSTRAINT fk_job_skills_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 8. APPLICATIONS TABLE
-- Foreign Key Behavior: ON DELETE RESTRICT on job_id and job_seeker_id to preserve application history.
-- ==================================================
CREATE TABLE applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    job_id INT NOT NULL,
    job_seeker_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
    match_percentage DECIMAL(5,2) DEFAULT 0.00,
    cover_letter TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_job_seeker_application UNIQUE (job_id, job_seeker_id),
    CONSTRAINT fk_application_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE RESTRICT,
    CONSTRAINT fk_application_seeker FOREIGN KEY (job_seeker_id) REFERENCES job_seekers(id) ON DELETE RESTRICT,
    CONSTRAINT chk_app_status CHECK (status IN ('APPLIED', 'UNDER_REVIEW', 'SHORTLISTED', 'INTERVIEW', 'SELECTED', 'REJECTED', 'WITHDRAWN')),
    CONSTRAINT chk_match_percentage CHECK (match_percentage BETWEEN 0 AND 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 9. INTERVIEWS TABLE
-- Foreign Key Behavior: ON DELETE RESTRICT on application_id to protect interview records.
-- ==================================================
CREATE TABLE interviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    interview_date DATE NOT NULL,
    interview_time TIME NOT NULL,
    interview_type VARCHAR(20) NOT NULL,
    meeting_link VARCHAR(255),
    location VARCHAR(255),
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interview_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE RESTRICT,
    CONSTRAINT chk_interview_type CHECK (interview_type IN ('ONLINE', 'OFFLINE')),
    CONSTRAINT chk_interview_status CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- 10. NOTIFICATIONS TABLE
-- Foreign Key Behavior: ON DELETE CASCADE on user_id (deleting a user removes their notifications).
-- ==================================================
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(30) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_notification_type CHECK (notification_type IN ('NEW_APPLICATION', 'APPLICATION_STATUS', 'INTERVIEW', 'JOB_EXPIRY', 'GENERAL'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- ==================================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

CREATE INDEX idx_jobs_title ON jobs(title);
CREATE INDEX idx_jobs_location ON jobs(location);
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_deadline ON jobs(application_deadline);

CREATE INDEX idx_applications_job_id ON applications(job_id);
CREATE INDEX idx_applications_job_seeker_id ON applications(job_seeker_id);
CREATE INDEX idx_applications_status ON applications(status);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

-- ==================================================
-- SAMPLE DATA INSERTION
-- Demo Password for all accounts: Password123
-- SHA-256 Hash of "Password123": 008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601
-- ==================================================

-- 1. Users
INSERT INTO users (name, email, password_hash, role, status) VALUES
('System Administrator', 'admin@smarthire.com', '008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'ADMIN', 'ACTIVE'),
('Sarah Jenkins', 'recruiter@techcorp.com', '008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'RECRUITER', 'ACTIVE'),
('John Doe', 'john.doe@email.com', '008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'JOB_SEEKER', 'ACTIVE'),
('Jane Smith', 'jane.smith@email.com', '008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'JOB_SEEKER', 'ACTIVE');

-- 2. Recruiter Profile
INSERT INTO recruiters (user_id, phone, designation) VALUES
(2, '+1-555-0192', 'Senior Technical Recruiter');

-- 3. Job Seeker Profiles
INSERT INTO job_seekers (user_id, phone, education, experience, location, bio, resume_path, profile_completion) VALUES
(3, '+1-555-0101', 'B.S. in Computer Science', '3 years software engineering experience in Java & Spring Boot', 'New York, NY', 'Passionate backend engineer dedicated to building scalable distributed systems.', '/resumes/john_doe_resume.pdf', 85),
(4, '+1-555-0102', 'M.S. in Information Technology', '2 years frontend web development experience in HTML, CSS, JavaScript', 'San Francisco, CA', 'Creative UI/UX & Frontend Developer crafting intuitive web interfaces.', '/resumes/jane_smith_resume.pdf', 90);

-- 4. Company
INSERT INTO companies (recruiter_id, company_name, description, location, website, industry, company_size) VALUES
(1, 'TechCorp Solutions', 'Leading enterprise cloud solution and software engineering firm.', 'San Francisco, CA', 'https://techcorp.example.com', 'Information Technology', '500-1000 employees');

-- 5. 10 Skills
INSERT INTO skills (skill_name) VALUES
('Java'),
('C++'),
('Python'),
('SQL'),
('HTML'),
('CSS'),
('JavaScript'),
('Spring Boot'),
('Git'),
('REST API');

-- 6. 3 Jobs
INSERT INTO jobs (company_id, title, description, location, salary_min, salary_max, job_type, experience_required, education_required, application_deadline, status) VALUES
(1, 'Java Developer', 'Looking for an experienced Java Developer proficient in enterprise backend development, REST APIs, and SQL database design.', 'Remote', 90000.00, 120000.00, 'FULL_TIME', '2-4 years', 'B.S. in CS or equivalent', '2026-12-31', 'ACTIVE'),
(1, 'Python Developer', 'Seeking a Data & Backend Python Developer to work on asynchronous services, data pipelines, and RESTful APIs.', 'San Francisco, CA', 95000.00, 130000.00, 'FULL_TIME', '3+ years', 'B.S. in CS or Data Science', '2026-11-30', 'ACTIVE'),
(1, 'Frontend Developer', 'Join our dynamic UI team to build modern responsive web interfaces using HTML, CSS, JavaScript, and Web APIs.', 'New York, NY', 80000.00, 110000.00, 'FULL_TIME', '1-3 years', 'Bachelor degree in IT / Web Dev', '2026-10-31', 'ACTIVE');

-- 7. Job Skills Mapping
INSERT INTO job_skills (job_id, skill_id) VALUES
-- Java Developer (Java, SQL, Spring Boot, Git, REST API)
(1, 1), (1, 4), (1, 8), (1, 9), (1, 10),
-- Python Developer (Python, SQL, Git, REST API)
(2, 3), (2, 4), (2, 9), (2, 10),
-- Frontend Developer (HTML, CSS, JavaScript, Git, REST API)
(3, 5), (3, 6), (3, 7), (3, 9), (3, 10);

-- 8. Sample Application
INSERT INTO applications (job_id, job_seeker_id, status, match_percentage, cover_letter) VALUES
(1, 1, 'SHORTLISTED', 88.50, 'Dear Hiring Team, I am thrilled to apply for the Java Developer role. With 3 years of hands-on Java & SQL experience, I am confident in adding immediate value to TechCorp.');

-- 9. Sample Interview
INSERT INTO interviews (application_id, interview_date, interview_time, interview_type, meeting_link, location, notes, status) VALUES
(1, '2026-10-15', '10:00:00', 'ONLINE', 'https://meet.techcorp.example.com/interview-jdoe-java', 'Google Meet', 'Technical Round 1 focusing on Core Java, Data Structures, and Database Queries.', 'SCHEDULED');

-- 10. Sample Notifications
INSERT INTO notifications (user_id, title, message, notification_type, is_read) VALUES
(2, 'New Application Received', 'John Doe submitted an application for the Java Developer position.', 'NEW_APPLICATION', FALSE),
(3, 'Interview Scheduled', 'Your interview for Java Developer at TechCorp Solutions has been scheduled for Oct 15, 2026.', 'INTERVIEW', FALSE);
