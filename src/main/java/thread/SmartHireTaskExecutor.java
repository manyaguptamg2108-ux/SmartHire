package thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Managed ExecutorService wrapper providing fixed thread pool execution and graceful lifecycle management.
 */
public class SmartHireTaskExecutor {

    private static final Logger LOGGER = Logger.getLogger(SmartHireTaskExecutor.class.getName());
    private static final int DEFAULT_POOL_SIZE = 5;

    private ExecutorService executorService;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public SmartHireTaskExecutor() {
        init(DEFAULT_POOL_SIZE);
    }

    public SmartHireTaskExecutor(int poolSize) {
        init(poolSize);
    }

    public synchronized void init(int poolSize) {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "smarthire-worker-" + threadNumber.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                }
            });
            LOGGER.info("SmartHireTaskExecutor initialized with fixed pool size: " + poolSize);
        }
    }

    public void submitTask(Runnable task) {
        if (task == null) return;
        if (executorService == null || executorService.isShutdown()) {
            LOGGER.warning("Attempted to submit background task to shutdown/uninitialized executor. Task ignored.");
            return;
        }
        executorService.submit(task);
    }

    public synchronized boolean isShutdown() {
        return executorService == null || executorService.isShutdown();
    }

    public synchronized void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            LOGGER.info("Shutting down SmartHireTaskExecutor background worker threads...");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.warning("ExecutorService did not terminate in 5 seconds; forcing immediate shutdown...");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            LOGGER.info("SmartHireTaskExecutor shutdown completed.");
        }
    }
}
