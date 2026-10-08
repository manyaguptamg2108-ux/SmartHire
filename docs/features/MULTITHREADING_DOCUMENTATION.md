# SmartHire - Multithreading & Synchronization Architecture

## 1. Executive Summary & Purpose
Multithreading in **SmartHire** is used to decouple time-consuming background operations (such as sending user notifications, updating global analytics, and tracking job application statistics) from the main HTTP request/response thread pool. This ensures that user actions (like submitting a job application or changing candidate status) respond instantly without waiting for synchronous I/O operations.

---

## 2. Architecture & Components

```text
HTTP Request (Servlet)
        │
        ▼
Service Layer (ApplicationService)
        │
        ▼
BackgroundTaskManager (Facade)
        │
        ▼
SmartHireTaskExecutor (Fixed Pool ExecutorService: 5 Threads)
   ├── smarthire-worker-1 ──► NotificationTask ──► NotificationService & DAO
   ├── smarthire-worker-2 ──► NotificationTask ──► NotificationService & DAO
   ├── smarthire-worker-3 ──► NotificationTask ──► NotificationService & DAO
   └── smarthire-worker-N ──► ThreadSafeApplicationTracker
```

### Component Breakdown
1. **`SmartHireTaskExecutor.java`**:
   - Encapsulates Java `ExecutorService` configured with a fixed thread pool of 5 worker threads (`Executors.newFixedThreadPool(5)`).
   - Custom `ThreadFactory` assigns clean daemon thread names (`smarthire-worker-1`, `smarthire-worker-2`, etc.).
   - Implements graceful shutdown using `shutdown()`, `awaitTermination(5, TimeUnit.SECONDS)`, and `shutdownNow()`.
2. **`NotificationTask.java`**:
   - Implements `Runnable`. Performs asynchronous notification creation and persistence.
   - Instantiates an independent `NotificationService` instance per execution (utilizing thread-local JDBC connections to prevent database connection sharing).
   - Safely catches exceptions internally so background thread failures never propagate to user requests.
3. **`ThreadSafeApplicationTracker.java`**:
   - Maintains thread-safe metric counters and application statistics.
   - **`AtomicInteger`**: Used for `totalTasksProcessed` and `totalNotificationsSent` to provide lock-free thread-safe updates.
   - **`ConcurrentHashMap<Integer, AtomicInteger>`**: Tracks application counts per job ID safely under high concurrency.
   - **`synchronized` Methods**: Demonstrates mutual exclusion (`synchronized void incrementSynchronizedCounter()`) to eliminate lost updates.
4. **`BackgroundTaskManager.java`**:
   - Central singleton facade for initializing, shutting down, and submitting background tasks.
5. **`SmartHireAppListener.java`**:
   - Annotated with `@WebListener` implementing `ServletContextListener`.
   - On application startup (`contextInitialized`): Initializes thread pool.
   - On application shutdown/undeployment (`contextDestroyed`): Invokes graceful executor shutdown to prevent thread leaks in Tomcat.

---

## 3. Thread Safety & Synchronization Precautions

1. **No Shared JDBC Connections**:
   Worker threads do NOT share open JDBC `Connection` or `ResultSet` instances. Each `NotificationTask` creates its own service/DAO instance which opens and closes its own connection via `DBUtil`.
2. **No HTTP Request/Session Leaks**:
   Worker threads never store or reference `HttpServletRequest` or `HttpSession` objects. Only primitive IDs and strings are passed into task constructors.
3. **Preventing Lost Updates**:
   Concurrent updates to shared counters use `AtomicInteger.incrementAndGet()`, `ConcurrentHashMap.computeIfAbsent()`, and `synchronized` blocks.

---

## 4. Verification
Executed `util.MultithreadingTest` verifying:
- [x] ExecutorService Initialization with fixed pool size of 5
- [x] Concurrent Task Execution across 5 worker threads (`smarthire-worker-1..5`)
- [x] Synchronized Counter Accuracy (10 threads $\times$ 100 increments = 1000, 0 lost updates)
- [x] ConcurrentHashMap & AtomicInteger Consistency under concurrent writes
- [x] Asynchronous Notification Persistence to database
- [x] Graceful Executor Shutdown on container undeploy
