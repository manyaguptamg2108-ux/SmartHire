# SmartHire JDBC & DAO Architecture Documentation

## 1. Overview
The SmartHire persistence layer is built using raw JDBC (Java Database Connectivity) with standard Data Access Object (DAO) design patterns. It provides complete CRUD (Create, Read, Update, Delete) capability for all 10 domain entities while preventing SQL Injection vulnerabilities.

---

## 2. SQL Injection Safety Audit
All DAO classes enforce 100% Parameterized Queries via `java.sql.PreparedStatement`. 

### Query Construction Rules:
- **No Input Concatenation**: Zero user-supplied strings or integers are concatenated directly into SQL strings (e.g. `"WHERE email = '" + email + "'"` is strictly prohibited).
- **Placeholder Binding**: Every parameter uses positional `?` placeholders bound via typed setter methods (`setString()`, `setInt()`, `setDouble()`, `setDate()`, `setTime()`, `setTimestamp()`, `setObject()`).
- **Dynamic Search Safety**: Dynamic query builders in `JobDAO.searchJobs()` append `?` parameter markers conditionally to a `StringBuilder` and pass arguments in an isolated parameter array.

### Verified PreparedStatement Audit Matrix:

| DAO Class | SQL Statement Type | PreparedStatement Usage |
|---|---|---|
| `UserDAO` | `SELECT * FROM users WHERE email = ?` | `stmt.setString(1, email)` |
| `UserDAO` | `INSERT INTO users (name, email, password_hash, role, status) VALUES (?, ?, ?, ?, ?)` | `stmt.setString(1..5, ...)` |
| `JobSeekerDAO` | `SELECT js.*, u.* FROM job_seekers js JOIN users u ON js.user_id = u.id WHERE js.user_id = ?` | `stmt.setInt(1, userId)` |
| `JobDAO` | `SELECT * FROM jobs WHERE 1=1 AND (LOWER(title) LIKE ? OR LOWER(description) LIKE ?) AND LOWER(location) LIKE ? ...` | `stmt.setObject(i+1, param)` |
| `JobSkillsDAO` | `INSERT INTO job_skills (job_id, skill_id) VALUES (?, ?)` | `stmt.setInt(1, jobId); stmt.setInt(2, skillId)` |
| `ApplicationDAO` | `UPDATE applications SET status = ? WHERE id = ?` | `stmt.setString(1, status); stmt.setInt(2, id)` |
| `InterviewDAO` | `INSERT INTO interviews (...) VALUES (?, ?, ?, ?, ?, ?, ?, ?)` | `stmt.setDate(2, ...); stmt.setTime(3, ...)` |
| `NotificationDAO` | `UPDATE notifications SET is_read = TRUE WHERE user_id = ?` | `stmt.setInt(1, userId)` |

---

## 3. Resource Management & Exception Handling
- **Try-With-Resources**: All JDBC operations wrap `Connection`, `PreparedStatement`, and `ResultSet` objects inside Java `try-with-resources` blocks to guarantee automatic closure, preventing connection leaks.
- **Custom Exceptions**: Database errors are wrapped in `DatabaseException` (extending `Exception`), preserving the original `SQLException` cause and stack trace without swallowing failures.
- **Connection Reuse**: Connections are opened on demand per DAO operation and cleanly closed immediately after execution.

---

## 4. Configuration & Credentials Management
Database connection parameters in `DBUtil` are configured flexibly without hardcoding passwords in Git:
- `DB_URL` (Environment Variable) / `-Ddb.url` (System Property)
- `DB_USER` (Environment Variable) / `-Ddb.user` (System Property)
- `DB_PASSWORD` (Environment Variable) / `-Ddb.password` (System Property)
- **Local Dev Fallback**: Default fallback connection targets `jdbc:mariadb://localhost:3306/smarthire`.
