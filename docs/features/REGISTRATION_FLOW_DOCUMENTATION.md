# SmartHire – Automatic Registration → Login → Dashboard Flow Documentation

## Overview

SmartHire implements an automatic registration and session initialization architecture. When a user submits the public registration form at `/register`, the system performs real-time validation, hashes the password via SHA-256, inserts the user into MariaDB/MySQL, initializes their role-specific profile row (`job_seekers` or `recruiters`), creates a fresh `HttpSession`, and immediately redirects the user to `/dashboard`.

---

## 1. Request & Processing Lifecycle

```text
       Registration Form (/register)
                    ↓
       Input Validation & Security Checks
       - Name required
       - Valid email format & uniqueness check
       - Password >= 6 chars & confirmation match
       - Public role validation (JOB_SEEKER / RECRUITER)
       - ADMIN registration blocked
                    ↓
       SHA-256 Password Hashing (PasswordUtil)
                    ↓
       Database Insertion via PreparedStatement
       - Insert into `users` table
       - Auto-generate primary key `user_id`
       - Insert initial profile into `job_seekers` or `recruiters`
                    ↓
       Session Initialization & Fixation Defense
       - Invalidate existing session (if present)
       - Create fresh HttpSession
       - Set attributes: `userId`, `userRole`, `userName`
                    ↓
       Automatic Redirect (sendRedirect)
       - Redirect to `/dashboard`
       - Render role-specific dashboard (Job Seeker / Recruiter)
```

---

## 2. Comparison: Existing Login vs. Automatic Registration

| Sequence Step | Existing User Login (`/login`) | New User Registration (`/register`) |
| :--- | :--- | :--- |
| **Entry Point** | User navigates to `/login` | User navigates to `/register` |
| **Input Data** | Email & Password | Name, Email, Role, Password, Confirm Password |
| **Database Check** | Verifies existing record & password hash | Checks email uniqueness (`findByEmail`) |
| **Database Action**| Read query (`SELECT * FROM users WHERE email = ?`) | Write query (`INSERT INTO users ...` & profile row) |
| **Session Action** | Invalidates old session; sets `userId`, `userRole`, `userName` | Invalidates old session; sets `userId`, `userRole`, `userName` |
| **Redirection** | `response.sendRedirect(contextPath + "/dashboard")` | `response.sendRedirect(contextPath + "/dashboard")` |
| **User Action** | Immediate dashboard access | **Immediate dashboard access (NO 2nd login required)** |

---

## 3. Security Architecture & Rules

1. **Session Fixation Prevention**: `request.getSession(false).invalidate()` is invoked prior to `request.getSession(true)` during both login and auto-login after registration.
2. **Password Protection**: Passwords are never stored as plaintext and are cryptographically hashed using SHA-256 with salt foundation via `PasswordUtil.hashPassword()`.
3. **PreparedStatements**: SQL queries in `UserDAO`, `JobSeekerDAO`, and `RecruiterDAO` use parameterized bindings to guarantee protection against SQL Injection.
4. **Role Authorization Guard**: Public registration strictly restricts roles to `JOB_SEEKER` and `RECRUITER`. Any attempt to register as `ADMIN` is rejected with an `InvalidUserException`.
5. **Clean Validation Messaging**: Input errors (e.g. duplicate email, password mismatch) display clean user-facing error banners on the registration page without exposing SQL stack traces.

---

## 4. Profile Initialization

- **Job Seeker**: An initial `job_seekers` record linked by `user_id` is automatically inserted with 0% initial completion.
- **Recruiter**: An initial `recruiters` record linked by `user_id` is automatically inserted. Company profile completion can be filled out later via `/company-profile`.
