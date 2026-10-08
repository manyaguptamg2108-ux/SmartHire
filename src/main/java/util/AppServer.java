package util;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Single-command Embedded Tomcat server launcher for SmartHire.
 * Mounts the web application at http://localhost:8080/
 */
public class AppServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portProp = System.getProperty("port");
        if (portProp != null && !portProp.trim().isEmpty()) {
            port = Integer.parseInt(portProp.trim());
        }

        System.out.println("Starting SmartHire Embedded Localhost Server...");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir = new File(".");
        }

        String webappPath = webappDir.getAbsolutePath();

        // Mount main application at root context (/)
        Context rootCtx = tomcat.addWebapp("", webappPath);
        rootCtx.setParentClassLoader(AppServer.class.getClassLoader());

        // Add target/classes so Tomcat Servlet container registers @WebServlet annotations
        File classesDir = new File("target/classes");
        if (classesDir.exists()) {
            WebResourceRoot resources = new StandardRoot(rootCtx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/"));
            rootCtx.setResources(resources);
        }

        System.out.println("Web application mounted at path: " + webappPath);

        // Verify DB connection
        try {
            DBUtil.getConnection().close();
            System.out.println("✅ Database connection verified (MariaDB/H2).");
        } catch (Exception e) {
            System.err.println("⚠️ Database connection check warning: " + e.getMessage());
        }

        System.out.println("=================================================================");
        System.out.println("🚀 SmartHire Local Server live at:");
        System.out.println("   👉 http://localhost:" + port + "/");
        System.out.println("   👉 http://localhost:" + port + "/login");
        System.out.println("   👉 http://localhost:" + port + "/register");
        System.out.println("=================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
