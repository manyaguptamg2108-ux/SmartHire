# SmartHire - JDBC Transaction Management Architecture

## 1. Executive Summary & Purpose
Database Transactions in **SmartHire** ensure atomicity, consistency, isolation, and durability (ACID) across complex multi-step database operations. For instance, submitting a job application requires inserting both an `Application` row and a corresponding `Notification` row. Without transactions, a failure during notification insertion could leave an application in an inconsistent state without informing the user. With JDBC transactions, both operations execute under a single transaction boundary: either both succeed (**COMMIT**) or neither persists (**ROLLBACK**).

---

## 2. Architecture & Components

```text
Service Layer (ApplicationTransactionService)
        │
        ▼
TransactionManager.executeInTransaction(callback)
        │
        ├── 1. Connection conn = DBUtil.getConnection()
        ├── 2. conn.setAutoCommit(false)
        │
        ├── 3. Execute Callback Operations:
        │       ├── ApplicationDAO.save(conn, application)
        │       └── NotificationDAO.save(conn, notification)
        │
        ├── 4. Success? ──► conn.commit()
        └── 5. Error?   ──► conn.rollback()  ──► throw TransactionException
        │
        └── 6. finally  ──► conn.setAutoCommit(true); conn.close();
```

### Component Breakdown
1. **`TransactionManager.java`**:
   - Manages JDBC transaction lifecycle using explicit `Connection.setAutoCommit(false)`, `Connection.commit()`, `Connection.rollback()`, and connection closing.
   - Provides a functional interface abstraction:
     ```java
     public static <T> T executeInTransaction(TransactionCallback<T> callback) throws TransactionException
     ```
2. **`TransactionCallback.java`**:
   - `@FunctionalInterface` allowing lambda expressions to execute database queries on a single passed-in `Connection`.
3. **`TransactionException.java`**:
   - Custom exception extending `Exception` that wraps underlying `SQLException`s without exposing database connection strings or credentials to end users.
4. **Transaction-Aware DAO Overloads**:
   - `ApplicationDAO.save(Connection conn, Application app)`
   - `NotificationDAO.save(Connection conn, Notification notif)`
   - These methods use the provided `Connection` instance instead of requesting a new connection from `DBUtil`, ensuring all queries run within the active transaction boundary.
5. **`ApplicationTransactionService.java`**:
   - Implements atomic multi-step operations (`submitApplicationAtomic` and `submitApplicationForcedFailure`).

---

## 3. Atomic Workflow & Rollback Proof

### COMMIT Scenario (`submitApplicationAtomic`):
1. `TransactionManager` disables auto-commit (`setAutoCommit(false)`).
2. Inserts `Application` row $\rightarrow$ assigned ID generated.
3. Inserts `Notification` row on the SAME connection $\rightarrow$ assigned ID generated.
4. `conn.commit()` is invoked $\rightarrow$ both rows are atomically committed to the MariaDB/MySQL database.

### ROLLBACK Scenario (`submitApplicationForcedFailure`):
1. `TransactionManager` disables auto-commit (`setAutoCommit(false)`).
2. Inserts `Application` row successfully.
3. Intentionally attempts to insert `Notification` row with invalid user ID (`-999`), violating foreign key constraint `fk_notifications_users`.
4. `SQLException` caught at transaction boundary $\rightarrow$ `conn.rollback()` is invoked.
5. Verification confirms that the `Application` row inserted in step 2 is **completely ABSENT** from the database.

---

## 4. Concurrency & Thread-Safety with Step 8
- **Independent Connection Per Transaction**: Each transaction invocation obtains its own dedicated `Connection` from `DBUtil.getConnection()`.
- **No Shared Connections**: JDBC `Connection` instances are never stored in static fields or shared across worker threads in the `ExecutorService`.
- **No HTTP Leakage**: `HttpServletRequest` and `HttpSession` objects are never passed into background threads or transaction callbacks.

---

## 5. Automated Verification
Executed `util.TransactionTest` verifying:
- [x] `TransactionManager` initialization
- [x] Successful multi-step transaction execution
- [x] `COMMIT` persistence for Application & Notification
- [x] Forced Foreign-Key failure detection
- [x] `ROLLBACK` execution
- [x] Rolled-back Application completely absent from DB
- [x] Rolled-back Notification completely absent from DB
- [x] Connection release & cleanup
- [x] Existing DAO operations completely unaffected
