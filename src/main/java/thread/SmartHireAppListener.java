package thread;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Logger;

/**
 * Servlet Context Listener managing the lifecycle of background worker threads and ExecutorService upon deployment/undeployment.
 */
@WebListener
public class SmartHireAppListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(SmartHireAppListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("SmartHire Context Initialized: Starting background worker thread pool...");
        BackgroundTaskManager.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("SmartHire Context Destroyed: Gracefully shutting down background worker thread pool...");
        BackgroundTaskManager.shutdown();
    }
}
