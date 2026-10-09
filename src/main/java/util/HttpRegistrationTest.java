package util;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HttpRegistrationTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   HTTP REAL BROWSER REGISTRATION & LOGIN TEST    ");
        System.out.println("==================================================");

        long timestamp = System.currentTimeMillis();
        String name = "Browser Real Test User " + timestamp;
        String email = "browser_test_" + timestamp + "@smarthire.com";
        String password = "Password123";

        try {
            CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
            HttpClient client = HttpClient.newBuilder()
                    .cookieHandler(cookieManager)
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();

            // 1. Send HTTP POST Register Request
            String formBody = "name=" + URLEncoder.encode(name, StandardCharsets.UTF_8)
                    + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8)
                    + "&confirmPassword=" + URLEncoder.encode(password, StandardCharsets.UTF_8)
                    + "&role=JOB_SEEKER";

            HttpRequest registerRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/SmartHire/register"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            HttpResponse<String> response = client.send(registerRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("[STEP 1] POST /SmartHire/register -> Status: " + response.statusCode());
            String location = response.headers().firstValue("Location").orElse("");
            System.out.println(" -> Redirect Location: " + location);

            if (response.statusCode() == 302 && location.endsWith("/dashboard")) {
                System.out.println("[PASS] HTTP Registration successful, redirected to /dashboard");
            } else {
                System.err.println("[FAIL] Expected 302 Redirect to /dashboard but got: " + response.statusCode() + " Location: " + location);
                return;
            }

            // 2. Query Live MariaDB database for the registered user
            System.out.println("\n[STEP 2] Direct Querying MariaDB smarthire.users table...");
            int userId = 0;
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, name, email, role, status FROM users WHERE email = ?")) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("id");
                        System.out.println(" -> Verified Row in users Table:");
                        System.out.println("    ID: " + userId + " | Name: " + rs.getString("name") + " | Email: " + rs.getString("email") + " | Role: " + rs.getString("role"));
                        System.out.println("[PASS] Live user row EXISTS in MariaDB users table");
                    } else {
                        System.err.println("[FAIL] Live user row NOT FOUND in MariaDB!");
                        return;
                    }
                }
            }

            // 3. Query Live MariaDB database for job_seekers profile row
            System.out.println("\n[STEP 3] Direct Querying MariaDB smarthire.job_seekers profile table...");
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM job_seekers WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("[PASS] Corresponding job_seekers profile row EXISTS in MariaDB");
                    } else {
                        System.err.println("[FAIL] job_seekers profile row NOT FOUND for User ID: " + userId);
                        return;
                    }
                }
            }

            // 4. Test HTTP POST Login Request with SAME credentials
            System.out.println("\n[STEP 4] Testing HTTP POST Login with SAME credentials...");
            String loginBody = "email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/SmartHire/login"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                    .build();

            HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(" -> POST /SmartHire/login -> Status: " + loginResponse.statusCode());
            String loginLocation = loginResponse.headers().firstValue("Location").orElse("");
            System.out.println(" -> Redirect Location: " + loginLocation);

            if (loginResponse.statusCode() == 302 && loginLocation.endsWith("/dashboard")) {
                System.out.println("[PASS] HTTP Login with SAME credentials successful, redirected to /dashboard");
            } else {
                System.err.println("[FAIL] HTTP Login failed! Status: " + loginResponse.statusCode());
                return;
            }

            System.out.println("\n==================================================");
            System.out.println("   REAL BROWSER HTTP REGISTRATION TEST PASSED!    ");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("[FAIL] HttpRegistrationTest Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
