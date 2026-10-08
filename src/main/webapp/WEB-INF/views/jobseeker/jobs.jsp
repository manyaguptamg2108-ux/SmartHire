<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse & Live Search Jobs - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .source-tabs {
            display: flex;
            gap: 1rem;
            margin-bottom: 1.5rem;
            border-bottom: 2px solid var(--border-color, #e2e8f0);
            padding-bottom: 0.5rem;
        }
        .source-tab {
            padding: 0.5rem 1.25rem;
            font-weight: 600;
            color: #64748b;
            text-decoration: none;
            border-radius: 6px;
            transition: all 0.2s ease;
        }
        .source-tab.active {
            color: #2563eb;
            background-color: #eff6ff;
            border-bottom: 2px solid #2563eb;
        }
        .badge-live {
            background-color: #0284c7;
            color: #ffffff;
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
            border-radius: 4px;
            font-weight: 600;
        }
        .badge-local {
            background-color: #16a34a;
            color: #ffffff;
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
            border-radius: 4px;
            font-weight: 600;
        }
        .live-api-banner {
            background: #f8fafc;
            border-left: 4px solid #0284c7;
            padding: 1rem 1.25rem;
            border-radius: 6px;
            margin-bottom: 1.5rem;
            color: #334155;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <!-- Search Filter Form Card -->
                <div class="card mb-4" style="margin-bottom: 1.5rem;">
                    <form action="${pageContext.request.contextPath}/jobs" method="get" id="jobSearchForm">
                        <input type="hidden" name="source" value="${not empty source ? source : 'all'}" id="sourceInput" />
                        <div class="grid-cols-4" style="margin-bottom: 0; align-items: flex-end;">
                            <div class="form-group" style="margin-bottom: 0;">
                                <label class="form-label">Keyword / Title</label>
                                <input type="text" name="keyword" class="form-control" placeholder="Java Developer, Python, Engineer..." value="${keyword}" />
                            </div>
                            <div class="form-group" style="margin-bottom: 0;">
                                <label class="form-label">Location</label>
                                <input type="text" name="location" class="form-control" placeholder="Delhi, Noida, Bangalore, Remote..." value="${location}" />
                            </div>
                            <div class="form-group" style="margin-bottom: 0;">
                                <label class="form-label">Job Type</label>
                                <select name="jobType" class="form-control">
                                    <option value="">All Job Types</option>
                                    <option value="FULL_TIME" ${jobType == 'FULL_TIME' ? 'selected' : ''}>Full Time</option>
                                    <option value="PART_TIME" ${jobType == 'PART_TIME' ? 'selected' : ''}>Part Time</option>
                                    <option value="CONTRACT" ${jobType == 'CONTRACT' ? 'selected' : ''}>Contract</option>
                                    <option value="INTERNSHIP" ${jobType == 'INTERNSHIP' ? 'selected' : ''}>Internship</option>
                                    <option value="REMOTE" ${jobType == 'REMOTE' ? 'selected' : ''}>Remote</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary" style="height: 42px;" id="searchBtn">
                                🔍 Search Jobs
                            </button>
                        </div>
                    </form>
                </div>

                <!-- Source Selection Tabs -->
                <div class="source-tabs">
                    <a href="${pageContext.request.contextPath}/jobs?source=all&keyword=${keyword}&location=${location}&jobType=${jobType}" class="source-tab ${empty source || source == 'all' ? 'active' : ''}">
                        🌐 All Opportunities
                    </a>
                    <a href="${pageContext.request.contextPath}/jobs?source=local&keyword=${keyword}&location=${location}&jobType=${jobType}" class="source-tab ${source == 'local' ? 'active' : ''}">
                        💼 SmartHire Jobs
                    </a>
                    <a href="${pageContext.request.contextPath}/jobs?source=live&keyword=${keyword}&location=${location}&jobType=${jobType}" class="source-tab ${source == 'live' ? 'active' : ''}">
                        ⚡ Live Online Listings (API)
                    </a>
                </div>

                <!-- Live Search API Status Notice -->
                <c:if test="${not empty liveSearchStatus}">
                    <div class="live-api-banner">
                        <strong>ℹ️ Live Search Info:</strong> ${liveSearchStatus}
                    </div>
                </c:if>

                <!-- SECTION 1: SmartHire Local Database Jobs -->
                <c:if test="${source != 'live' && not empty jobs}">
                    <h3 style="font-weight: 700; font-size: 1.1rem; margin-bottom: 1rem; color: #1e293b;">
                        🏢 SmartHire Verified Jobs (${jobs.size()})
                    </h3>
                    <div class="jobs-grid" style="margin-bottom: 2rem;">
                        <c:forEach var="job" items="${jobs}">
                            <div class="card job-card card-hover">
                                <div>
                                    <div class="job-card-header">
                                        <div>
                                            <h3 class="job-title">${job.title}</h3>
                                            <span class="company-name">SmartHire Verified</span>
                                        </div>
                                        <span class="badge badge-local">Local Job</span>
                                    </div>

                                    <div class="job-meta">
                                        <span>📍 ${job.location}</span>
                                        <span>💰 ₹ ${job.salaryMin} - ₹ ${job.salaryMax}</span>
                                    </div>

                                    <p style="font-size: 0.875rem; color: var(--text-muted); margin-bottom: 1rem; line-height: 1.4;">
                                        <c:choose>
                                            <c:when test="${job.description.length() > 120}">
                                                ${job.description.substring(0, 120)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${job.description}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>

                                <div style="border-top: 1px solid var(--border-color); padding-top: 1rem; display: flex; justify-content: space-between; align-items: center;">
                                    <span style="font-size: 0.8rem; color: var(--text-muted);">Deadline: ${job.applicationDeadline}</span>
                                    <a href="${pageContext.request.contextPath}/job-detail?id=${job.id}" class="btn btn-sm btn-primary">
                                        View Details &amp; Apply
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>

                <!-- SECTION 2: Real Live External Online Jobs (Adzuna API) -->
                <c:if test="${source != 'local' && not empty liveJobs}">
                    <h3 style="font-weight: 700; font-size: 1.1rem; margin-bottom: 1rem; color: #0284c7;">
                        ⚡ Live Online Listings (${liveJobs.size()})
                    </h3>
                    <div class="jobs-grid" style="margin-bottom: 2rem;">
                        <c:forEach var="liveJob" items="${liveJobs}">
                            <div class="card job-card card-hover" style="border-top: 3px solid #0284c7;">
                                <div>
                                    <div class="job-card-header">
                                        <div>
                                            <h3 class="job-title">${liveJob.title}</h3>
                                            <span class="company-name">${not empty liveJob.companyName ? liveJob.companyName : 'External Recruiter'}</span>
                                        </div>
                                        <span class="badge badge-live">External Live</span>
                                    </div>

                                    <div class="job-meta">
                                        <span>📍 ${not empty liveJob.location ? liveJob.location : 'India'}</span>
                                        <span>💰 ${liveJob.formattedSalary}</span>
                                    </div>

                                    <p style="font-size: 0.875rem; color: var(--text-muted); margin-bottom: 1rem; line-height: 1.4;">
                                        <c:choose>
                                            <c:when test="${not empty liveJob.description && liveJob.description.length() > 120}">
                                                ${liveJob.description.substring(0, 120)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${not empty liveJob.description ? liveJob.description : 'Click Apply on Source to view full external job requirements.'}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>

                                <div style="border-top: 1px solid var(--border-color); padding-top: 1rem; display: flex; justify-content: space-between; align-items: center;">
                                    <span style="font-size: 0.8rem; color: #0284c7; font-weight: 600;">Source: ${liveJob.sourceName}</span>
                                    <c:choose>
                                        <c:when test="${not empty liveJob.redirectUrl}">
                                            <a href="${liveJob.redirectUrl}" target="_blank" rel="noopener noreferrer" class="btn btn-sm btn-secondary" style="background-color: #0ea5e9; color: white; border: none;">
                                                Apply on Source ↗
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="btn btn-sm btn-outline disabled">Link N/A</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>

                <!-- Empty State Handling -->
                <c:if test="${empty jobs && empty liveJobs}">
                    <div class="card empty-state">
                        <div class="empty-icon">🔍</div>
                        <h3 class="empty-title">No Matching Opportunities Found</h3>
                        <p class="empty-description">Try adjusting your search keywords (e.g. "Java", "Python") or location filters (e.g. "Delhi", "Remote") to locate open listings.</p>
                        <a href="${pageContext.request.contextPath}/jobs" class="btn btn-secondary">Clear All Filters</a>
                    </div>
                </c:if>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
    <script>
        document.getElementById('jobSearchForm')?.addEventListener('submit', function() {
            var btn = document.getElementById('searchBtn');
            if (btn) {
                btn.innerHTML = '⏳ Searching live jobs...';
                btn.style.opacity = '0.8';
            }
        });
        
        // Clean raw unicode escapes in job titles (e.g. \ud83c\udde9)
        document.querySelectorAll('.job-title').forEach(function(el) {
            if (el.textContent.includes('\\u')) {
                try {
                    el.textContent = el.textContent.replace(/\\u([0-9a-fA-F]{4})/g, function(m, g) {
                        return String.fromCharCode(parseInt(g, 16));
                    });
                } catch(e) {}
            }
        });
    </script>
</body>
</html>
