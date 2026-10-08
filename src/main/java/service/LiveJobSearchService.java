package service;

import model.LiveJobResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * High-level Live Job Search Service managing external API queries, caching, and result mapping.
 */
public class LiveJobSearchService {

    private final AdzunaJobApiClient apiClient;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 10 * 60 * 1000L; // 10 minutes

    private static class CacheEntry {
        final List<LiveJobResult> results;
        final long timestamp;

        CacheEntry(List<LiveJobResult> results) {
            this.results = results;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }

    public LiveJobSearchService() {
        this.apiClient = new AdzunaJobApiClient();
    }

    public LiveJobSearchService(AdzunaJobApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Returns true if external live job search API credentials are configured.
     */
    public boolean isApiConfigured() {
        return apiClient != null && apiClient.isConfigured();
    }

    /**
     * Executes real live online job search with server-side caching.
     *
     * @param query Search query keywords
     * @param location Location filter
     * @param jobType Employment type filter
     * @param page Page index
     * @return List of real LiveJobResult items
     * @throws Exception if API fails or credentials missing
     */
    public List<LiveJobResult> searchLiveJobs(String query, String location, String jobType, int page) throws Exception {
        if (!isApiConfigured()) {
            throw new IllegalStateException("Live job search API is not configured. (Set ADZUNA_APP_ID & ADZUNA_APP_KEY)");
        }

        String cacheKey = buildCacheKey(query, location, jobType, page);
        CacheEntry entry = cache.get(cacheKey);
        if (entry != null && !entry.isExpired()) {
            return entry.results;
        }

        List<LiveJobResult> liveResults = apiClient.searchJobs(query, location, jobType, page);
        if (liveResults != null) {
            cache.put(cacheKey, new CacheEntry(liveResults));
        }
        return liveResults != null ? liveResults : Collections.emptyList();
    }

    private String buildCacheKey(String query, String location, String jobType, int page) {
        String q = (query != null) ? query.trim().toLowerCase() : "";
        String l = (location != null) ? location.trim().toLowerCase() : "";
        String t = (jobType != null) ? jobType.trim().toLowerCase() : "";
        return q + "|" + l + "|" + t + "|" + page;
    }

    /**
     * Clears all short-lived search cache entries.
     */
    public void clearCache() {
        cache.clear();
    }
}
