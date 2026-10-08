# SmartHire Authentication, Registration & Session Architecture Documentation

## 1. Overview
SmartHire features a secure, role-based authentication and HTTP Session management system protecting web routes against unauthorized access, credential forgery, and session fixation attacks.

---

## 2. Security & Hashing Architecture

### 2.1. SHA-256 Password Hashing (`util.PasswordUtil`)
- **Digest Algorithm**: `SHA-256` via `java.security.MessageDigest`.
- **Character Encoding**: UTF-8 (`StandardCharsets.UTF_8`).
- **Security Rules**:
  - Passwords are **never** stored as plain text.
  - Passwords and password hashes are **never** output in application logs.
  - `passwordHash` is strictly excluded from `User.toString()` implementations.
  - `passwordHash` is never exposed or sent to JSP view models.

---

## 3. Public Registration Flow (`AuthServlet` -> `UserService` -> `UserDAO`)

```
POST /register
    ↓
AuthServlet
    ↓
UserService.registerUser()
    ├── Validates input parameters (name, email, password length, match confirmation)
    ├── Checks email uniqueness in database
    ├── Restricts ADMIN registration (Throws InvalidUserException if role == ADMIN)
    ├── Hashes raw password using PasswordUtil.hashPassword()
    └── Persists User & Profile row (Status = ACTIVE)
    ↓
Redirect to /login with success message
```

---

## 4. Login Authentication & Session Flow (`AuthServlet` -> `UserService` -> `DashboardServlet`)

```
POST /login
    ↓
AuthServlet
    ↓
UserService.loginUser()
    ├── Validates email/password inputs
    ├── Finds User by email
    ├── Verifies User status == ACTIVE
    └── Verifies password match via PasswordUtil.verifyPassword()
    ↓
Session Protection
    ├── Invalidates any pre-existing session (Prevents Session Fixation)
    └── Creates fresh HttpSession (session.setAttribute("userId"), "userRole", "userName")
    ↓
Redirect to /dashboard
```

---

## 5. Protected Routes & Authorization

### 5.1. Session Utility (`util.AuthUtil`)
Provides reusable session inspection methods:
- `isLoggedIn(request)`
- `getCurrentUserId(request)`
- `getCurrentRole(request)`
- `hasRole(request, role)`

### 5.2. Protected Route Filter & Headers (`controller.AuthenticationFilter` & `DashboardServlet`)
- Routes `/dashboard` and protected sub-paths require an active authenticated `HttpSession`.
- Unauthenticated requests are redirected to `/login`.
- Applies anti-caching HTTP response headers to protect against browser back-button caching:
  ```http
  Cache-Control: no-cache, no-store, must-revalidate
  Pragma: no-cache
  Expires: 0
  ```

---

## 6. Role-Based Redirection Matrix

| Authenticated User Role | Target Route | Target View / Dashboard Placeholder |
|---|---|---|
| `JOB_SEEKER` | `/dashboard` | `WEB-INF/views/job-seeker-placeholder.jsp` |
| `RECRUITER` | `/dashboard` | `WEB-INF/views/recruiter-placeholder.jsp` |
| `ADMIN` | `/dashboard` | `WEB-INF/views/admin-placeholder.jsp` |

---

## 7. Logout Flow (`GET /logout`)
1. Fetches current session: `request.getSession(false)`.
2. Invalidates session: `session.invalidate()`.
3. Redirects user to `${pageContext.request.contextPath}/login`.
4. After logout, attempts to access `/dashboard` redirect back to `/login`.
