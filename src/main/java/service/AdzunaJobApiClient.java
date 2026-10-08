package service;

import model.LiveJobResult;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Multi-provider Live Job API Client supporting Adzuna REST API (when credentials are provided)
 * and seamless fallback to Open Live Job Providers (Remotive & Arbeitnow) when Adzuna keys are unconfigured.
 */
public class AdzunaJobApiClient {

    private static final String ADZUNA_BASE_URL = "https://api.adzuna.com/v1/api/jobs/";
    private static final String DEFAULT_COUNTRY = "in"; // India
    private static final String REMOTIVE_API_URL = "https://remotive.com/api/remote-jobs?limit=25";
    private static final String ARBEITNOW_API_URL = "https://www.arbeitnow.com/api/job-board-api";
    private static final int TIMEOUT_SECONDS = 8;

    private final HttpClient httpClient;

    public AdzunaJobApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Always returns true because the live search engine supports Adzuna credentials when provided,
     * and open public live job providers out-of-the-box.
     */
    public boolean isConfigured() {
        return true;
    }

    public boolean isAdzunaCredentialsProvided() {
        String appId = getAppId();
        String appKey = getAppKey();
        return appId != null && !appId.trim().isEmpty() && appKey != null && !appKey.trim().isEmpty();
    }

    public String getAppId() {
        String id = System.getenv("ADZUNA_APP_ID");
        if (id == null || id.trim().isEmpty()) {
            id = System.getProperty("adzuna.app.id");
        }
        return id != null ? id.trim() : null;
    }

    public String getAppKey() {
        String key = System.getenv("ADZUNA_APP_KEY");
        if (key == null || key.trim().isEmpty()) {
            key = System.getProperty("adzuna.app.key");
        }
        return key != null ? key.trim() : null;
    }

    /**
     * Executes real online job search query. Uses Adzuna REST API if credentials exist,
     * otherwise queries Open Public Live Job Search APIs.
     */
    public List<LiveJobResult> searchJobs(String query, String location, String jobType, int page) throws Exception {
        if (isAdzunaCredentialsProvided()) {
            try {
                return searchAdzunaJobs(query, location, jobType, page);
            } catch (Exception e) {
                // If Adzuna credentials fail or return HTTP 401/403, fallback to open live search
                return searchOpenLiveJobs(query, location, jobType);
            }
        }
        return searchOpenLiveJobs(query, location, jobType);
    }

    private List<LiveJobResult> searchAdzunaJobs(String query, String location, String jobType, int page) throws Exception {
        String appId = getAppId();
        String appKey = getAppKey();
        int pageIndex = Math.max(1, page);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(ADZUNA_BASE_URL).append(DEFAULT_COUNTRY).append("/search/").append(pageIndex);
        urlBuilder.append("?app_id=").append(URLEncoder.encode(appId, StandardCharsets.UTF_8));
        urlBuilder.append("&app_key=").append(URLEncoder.encode(appKey, StandardCharsets.UTF_8));
        urlBuilder.append("&results_per_page=20");

        if (query != null && !query.trim().isEmpty()) {
            urlBuilder.append("&what=").append(URLEncoder.encode(query.trim(), StandardCharsets.UTF_8));
        }

        if (location != null && !location.trim().isEmpty()) {
            urlBuilder.append("&where=").append(URLEncoder.encode(location.trim(), StandardCharsets.UTF_8));
        }

        if (jobType != null && !jobType.trim().isEmpty() && !"all".equalsIgnoreCase(jobType)) {
            String normalizedType = jobType.trim().toLowerCase();
            if (normalizedType.contains("full") || normalizedType.contains("permanent")) {
                urlBuilder.append("&full_time=1");
            } else if (normalizedType.contains("part")) {
                urlBuilder.append("&part_time=1");
            } else if (normalizedType.contains("contract")) {
                urlBuilder.append("&contract=1");
            }
        }

        URI requestUri = URI.create(urlBuilder.toString());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestUri)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .header("Accept", "application/json")
                .header("User-Agent", "SmartHire-JobPortal/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401 || response.statusCode() == 403) {
            throw new IllegalStateException("Invalid or unauthorized Adzuna API credentials.");
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException("Adzuna API HTTP error: " + response.statusCode());
        }

        return parseAdzunaJsonResponse(response.body());
    }

    private List<LiveJobResult> searchOpenLiveJobs(String query, String location, String jobType) {
        List<LiveJobResult> results = new ArrayList<>();
        
        // [1] Fetch from Remotive Public API
        try {
            String apiUrl = REMOTIVE_API_URL;
            if (query != null && !query.trim().isEmpty()) {
                apiUrl += "&search=" + URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .header("Accept", "application/json")
                    .header("User-Agent", "SmartHire-JobPortal/1.0")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                results.addAll(parseRemotiveJsonResponse(response.body(), query, location));
            }
        } catch (Exception ignored) {}

        // [2] Fetch from Arbeitnow Public API if needed
        if (results.size() < 10) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ARBEITNOW_API_URL))
                        .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                        .header("Accept", "application/json")
                        .header("User-Agent", "SmartHire-JobPortal/1.0")
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    results.addAll(parseArbeitnowJsonResponse(response.body(), query, location));
                }
            } catch (Exception ignored) {}
        }

        return results;
    }

    /**
     * Parses Remotive JSON API response.
     */
    private List<LiveJobResult> parseRemotiveJsonResponse(String jsonBody, String queryFilter, String locationFilter) {
        List<LiveJobResult> list = new ArrayList<>();
        if (jsonBody == null || !jsonBody.contains("\"jobs\":")) return list;

        int jobsIdx = jsonBody.indexOf("\"jobs\":");
        String jobsArray = jsonBody.substring(jobsIdx);
        int bracket = jobsArray.indexOf('[');
        if (bracket == -1) return list;

        List<String> objects = extractJsonObjectsFromArray(jobsArray.substring(bracket));
        String qLower = queryFilter != null ? queryFilter.trim().toLowerCase() : null;
        String lLower = locationFilter != null ? locationFilter.trim().toLowerCase() : null;

        for (String block : objects) {
            try {
                String title = extractJsonValue(block, "title");
                String company = extractJsonValue(block, "company_name");
                String location = extractJsonValue(block, "candidate_required_location");
                String url = extractJsonValue(block, "url");
                String type = extractJsonValue(block, "job_type");
                String salaryStr = extractJsonValue(block, "salary");
                String description = extractJsonValue(block, "description");
                String id = extractJsonValue(block, "id");

                if (location == null || location.isEmpty()) {
                    location = "Remote / India";
                }

                // Filter matching check
                boolean matchQuery = (qLower == null || qLower.isEmpty() ||
                        (title != null && title.toLowerCase().contains(qLower)) ||
                        (company != null && company.toLowerCase().contains(qLower)) ||
                        (description != null && description.toLowerCase().contains(qLower)));

                boolean matchLocation = (lLower == null || lLower.isEmpty() ||
                        (location != null && location.toLowerCase().contains(lLower)) ||
                        "remote".contains(lLower));

                if (matchQuery && matchLocation && title != null && url != null) {
                    LiveJobResult res = new LiveJobResult();
                    res.setExternalId("remotive-" + (id != null ? id : list.size() + 1));
                    res.setTitle(title);
                    res.setCompanyName(company != null ? company : "Online Tech Employer");
                    res.setLocation(location);
                    res.setRedirectUrl(url);
                    res.setDescription(description);
                    res.setJobType(type != null ? type.toUpperCase() : "FULL TIME");
                    res.setSourceName("Remotive Live Jobs");
                    
                    if (salaryStr != null && !salaryStr.isEmpty()) {
                        res.setCategory(salaryStr);
                    } else {
                        res.setCategory("Competitive Salary");
                    }
                    list.add(res);
                }
            } catch (Exception ignored) {}
        }
        return list;
    }

    /**
     * Parses Arbeitnow JSON API response.
     */
    private List<LiveJobResult> parseArbeitnowJsonResponse(String jsonBody, String queryFilter, String locationFilter) {
        List<LiveJobResult> list = new ArrayList<>();
        if (jsonBody == null || !jsonBody.contains("\"data\":")) return list;

        int dataIdx = jsonBody.indexOf("\"data\":");
        String dataArray = jsonBody.substring(dataIdx);
        int bracket = dataArray.indexOf('[');
        if (bracket == -1) return list;

        List<String> objects = extractJsonObjectsFromArray(dataArray.substring(bracket));
        String qLower = queryFilter != null ? queryFilter.trim().toLowerCase() : null;
        String lLower = locationFilter != null ? locationFilter.trim().toLowerCase() : null;

        for (String block : objects) {
            try {
                String title = extractJsonValue(block, "title");
                String company = extractJsonValue(block, "company_name");
                String location = extractJsonValue(block, "location");
                String url = extractJsonValue(block, "url");
                String description = extractJsonValue(block, "description");
                String slug = extractJsonValue(block, "slug");

                if (location == null || location.isEmpty()) {
                    location = "Remote / India";
                }

                boolean matchQuery = (qLower == null || qLower.isEmpty() ||
                        (title != null && title.toLowerCase().contains(qLower)) ||
                        (company != null && company.toLowerCase().contains(qLower)));

                boolean matchLocation = (lLower == null || lLower.isEmpty() ||
                        (location != null && location.toLowerCase().contains(lLower)) ||
                        "remote".contains(lLower));

                if (matchQuery && matchLocation && title != null && url != null) {
                    LiveJobResult res = new LiveJobResult();
                    res.setExternalId("arbeitnow-" + (slug != null ? slug : list.size() + 1));
                    res.setTitle(title);
                    res.setCompanyName(company != null ? company : "Global Tech Employer");
                    res.setLocation(location);
                    res.setRedirectUrl(url);
                    res.setDescription(description);
                    res.setJobType("FULL TIME");
                    res.setSourceName("Arbeitnow Live Jobs");
                    list.add(res);
                }
            } catch (Exception ignored) {}
        }
        return list;
    }

    /**
     * Light-weight JSON parser mapping Adzuna response schema to List<LiveJobResult>.
     */
    public List<LiveJobResult> parseAdzunaJsonResponse(String jsonBody) {
        List<LiveJobResult> results = new ArrayList<>();
        if (jsonBody == null || jsonBody.trim().isEmpty()) {
            return results;
        }

        int resultsIndex = jsonBody.indexOf("\"results\":");
        if (resultsIndex == -1) {
            return results;
        }

        String resultsJson = jsonBody.substring(resultsIndex);
        int startBracket = resultsJson.indexOf('[');
        if (startBracket == -1) return results;

        List<String> objectBlocks = extractJsonObjectsFromArray(resultsJson.substring(startBracket));

        for (String block : objectBlocks) {
            try {
                LiveJobResult job = new LiveJobResult();
                job.setExternalId(extractJsonValue(block, "id"));
                job.setTitle(extractJsonValue(block, "title"));
                job.setRedirectUrl(extractJsonValue(block, "redirect_url"));
                job.setDescription(extractJsonValue(block, "description"));
                job.setCreatedDate(extractJsonValue(block, "created"));
                job.setSourceName("Adzuna Live");

                String companyBlock = extractNestedObject(block, "company");
                if (companyBlock != null) {
                    job.setCompanyName(extractJsonValue(companyBlock, "display_name"));
                }
                if (job.getCompanyName() == null || job.getCompanyName().isEmpty()) {
                    job.setCompanyName(extractJsonValue(block, "company"));
                }

                String locBlock = extractNestedObject(block, "location");
                if (locBlock != null) {
                    job.setLocation(extractJsonValue(locBlock, "display_name"));
                }
                if (job.getLocation() == null || job.getLocation().isEmpty()) {
                    job.setLocation(extractJsonValue(block, "location"));
                }

                String catBlock = extractNestedObject(block, "category");
                if (catBlock != null) {
                    job.setCategory(extractJsonValue(catBlock, "label"));
                }

                String salMinStr = extractNumberValue(block, "salary_min");
                if (salMinStr != null) {
                    try { job.setSalaryMin(Double.parseDouble(salMinStr)); } catch (Exception ignored) {}
                }

                String salMaxStr = extractNumberValue(block, "salary_max");
                if (salMaxStr != null) {
                    try { job.setSalaryMax(Double.parseDouble(salMaxStr)); } catch (Exception ignored) {}
                }

                String contract = extractJsonValue(block, "contract_type");
                if (contract != null && !contract.trim().isEmpty()) {
                    job.setJobType(contract.toUpperCase().replace("_", " "));
                } else {
                    job.setJobType("FULL TIME");
                }

                if (job.getTitle() != null && !job.getTitle().trim().isEmpty()) {
                    results.add(job);
                }
            } catch (Exception ignored) {}
        }

        return results;
    }

    private List<String> extractJsonObjectsFromArray(String jsonArrayText) {
        List<String> blocks = new ArrayList<>();
        int depth = 0;
        int startIndex = -1;
        boolean inString = false;

        for (int i = 0; i < jsonArrayText.length(); i++) {
            char c = jsonArrayText.charAt(i);
            if (c == '"' && (i == 0 || jsonArrayText.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString) {
                if (c == '{') {
                    if (depth == 0) {
                        startIndex = i;
                    }
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0 && startIndex != -1) {
                        blocks.add(jsonArrayText.substring(startIndex, i + 1));
                        startIndex = -1;
                    }
                }
            }
        }
        return blocks;
    }

    private String extractJsonValue(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return unescapeJson(m.group(1));
        }
        return null;
    }

    private String extractNumberValue(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private String extractNestedObject(String json, String key) {
        int idx = json.indexOf("\"" + key + "\"");
        if (idx == -1) return null;
        int brace = json.indexOf('{', idx);
        if (brace == -1 || brace > idx + key.length() + 10) return null;

        int depth = 0;
        for (int i = brace; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return json.substring(brace, i + 1);
                }
            }
        }
        return null;
    }

    private String unescapeJson(String input) {
        if (input == null) return null;
        return input.replace("\\/", "/")
                .replace("\\\"", "\"")
                .replace("\\n", " ")
                .replace("\\r", " ")
                .replace("\\t", " ")
                .replaceAll("<[^>]*>", "") // strip HTML tags
                .trim();
    }
}
