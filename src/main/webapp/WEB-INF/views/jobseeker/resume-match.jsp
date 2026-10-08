<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart Resume Match - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <div class="card" style="margin-bottom: 1.5rem; background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%); color: #ffffff;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem; color: #ffffff;">✨ Smart Resume Matcher</h2>
                            <p style="opacity: 0.9; margin: 0; max-width: 650px;">
                                Upload your resume (PDF or DOCX). Our rule-based parser extracts your skills, education, and experience, matching you against active internal positions and live market jobs.
                            </p>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger" style="margin-bottom: 1.5rem;">
                        <span>⚠️</span> ${errorMessage}
                    </div>
                </c:if>
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success" style="margin-bottom: 1.5rem;">
                        <span>✓</span> ${successMessage}
                    </div>
                </c:if>

                <div class="grid-cols-3">
                    <!-- Upload Resume Card -->
                    <div class="card" style="grid-column: span 1;">
                        <div class="card-header">
                            <h3 class="card-title">Upload Resume</h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/resume-match" method="post" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="upload" />
                            <div class="form-group">
                                <label class="form-label">Select Document (.pdf or .docx)</label>
                                <input type="file" name="resumeFile" class="form-control" accept=".pdf,.docx" required style="padding: 0.5rem;" />
                                <small style="color: var(--text-muted); display: block; margin-top: 0.35rem; font-size: 0.8rem;">
                                    Max file size: 5MB
                                </small>
                            </div>
                            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem;">
                                🔍 Extract & Match Jobs
                            </button>
                        </form>

                        <c:if test="${not empty resumeProfile}">
                            <div style="margin-top: 2rem; border-top: 1px solid var(--border-color); padding-top: 1rem;">
                                <h4 style="font-weight: 700; font-size: 0.95rem; margin-bottom: 0.75rem;">Extracted Resume Profile</h4>
                                
                                <c:if test="${not empty resumeProfile.name}">
                                    <p style="font-size: 0.85rem; margin-bottom: 0.35rem;">
                                        <strong>Name:</strong> ${resumeProfile.name}
                                    </p>
                                </c:if>
                                <c:if test="${not empty resumeProfile.email}">
                                    <p style="font-size: 0.85rem; margin-bottom: 0.35rem;">
                                        <strong>Email:</strong> ${resumeProfile.email}
                                    </p>
                                </c:if>
                                <c:if test="${not empty resumeProfile.phone}">
                                    <p style="font-size: 0.85rem; margin-bottom: 0.75rem;">
                                        <strong>Phone:</strong> ${resumeProfile.phone}
                                    </p>
                                </c:if>

                                <div style="margin-bottom: 1rem;">
                                    <strong style="font-size: 0.85rem; display: block; margin-bottom: 0.35rem;">Extracted Skills:</strong>
                                    <div style="display: flex; flex-wrap: wrap; gap: 0.35rem;">
                                        <c:forEach var="skill" items="${resumeProfile.skills}">
                                            <span class="badge badge-primary" style="font-size: 0.75rem;">${skill}</span>
                                        </c:forEach>
                                        <c:if test="${empty resumeProfile.skills}">
                                            <span style="font-size: 0.8rem; color: var(--text-muted);">No matching skills found in dictionary.</span>
                                        </c:if>
                                    </div>
                                </div>

                                <c:if test="${not empty resumeProfile.education}">
                                    <div style="margin-bottom: 0.75rem;">
                                        <strong style="font-size: 0.85rem; display: block; margin-bottom: 0.25rem;">Education:</strong>
                                        <ul style="padding-left: 1.2rem; font-size: 0.8rem; margin: 0;">
                                            <c:forEach var="edu" items="${resumeProfile.education}">
                                                <li>${edu}</li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>

                                <c:if test="${not empty sessionScope.userRole && sessionScope.userRole == 'JOB_SEEKER'}">
                                    <form action="${pageContext.request.contextPath}/resume-match" method="post" style="margin-top: 1rem;">
                                        <input type="hidden" name="action" value="updateProfile" />
                                        <button type="submit" class="btn btn-secondary" style="width: 100%; font-size: 0.85rem;">
                                            💾 Sync to My Profile
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                        </c:if>
                    </div>

                    <!-- Job Recommendations Results -->
                    <div class="card" style="grid-column: span 2;">
                        <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
                            <h3 class="card-title">Top Recommended Jobs</h3>
                            <c:if test="${not empty recommendations}">
                                <span class="badge badge-success">${recommendations.size()} Matches Found</span>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${not empty recommendations}">
                                <div style="display: flex; flex-direction: column; gap: 1rem;">
                                    <c:forEach var="rec" items="${recommendations}">
                                        <div style="border: 1px solid var(--border-color); border-radius: 8px; padding: 1rem; background: var(--bg-card, #ffffff);">
                                            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">
                                                <div>
                                                    <h4 style="margin: 0 0 0.25rem 0; font-size: 1.1rem; font-weight: 700; color: var(--text-main, #111827);">
                                                        ${rec.jobTitle}
                                                    </h4>
                                                    <div style="font-size: 0.85rem; color: var(--text-muted); font-weight: 500;">
                                                        🏢 ${rec.companyName} &nbsp;•&nbsp; 📍 ${rec.location} &nbsp;•&nbsp; 💵 ${rec.salaryText}
                                                    </div>
                                                </div>
                                                <div style="text-align: right;">
                                                    <span class="badge ${rec.matchPercentage >= 75 ? 'badge-success' : (rec.matchPercentage >= 50 ? 'badge-primary' : 'badge-warning')}" style="font-size: 0.9rem; padding: 0.35rem 0.65rem;">
                                                        ${rec.matchPercentage}% Match
                                                    </span>
                                                    <c:if test="${rec.external}">
                                                        <span class="badge badge-secondary" style="font-size: 0.75rem; margin-left: 0.25rem;">Live Market</span>
                                                    </c:if>
                                                </div>
                                            </div>

                                            <div style="margin: 0.75rem 0; font-size: 0.85rem; background: #f8fafc; padding: 0.65rem; border-radius: 6px; border-left: 3px solid #3b82f6;">
                                                <strong>Why This Job Matches:</strong> ${rec.matchExplanation}
                                            </div>

                                            <div style="display: flex; gap: 1rem; flex-wrap: wrap; margin-bottom: 0.75rem; font-size: 0.8rem;">
                                                <c:if test="${not empty rec.matchedSkills}">
                                                    <div>
                                                        <span style="color: #15803d; font-weight: 600;">✓ Matched Skills:</span>
                                                        <c:forEach var="ms" items="${rec.matchedSkills}">
                                                            <span style="display: inline-block; background: #dcfce7; color: #166534; padding: 2px 6px; border-radius: 4px; margin-right: 4px; font-weight: 500;">${ms}</span>
                                                        </c:forEach>
                                                    </div>
                                                </c:if>

                                                <c:if test="${not empty rec.missingSkills}">
                                                    <div>
                                                        <span style="color: #b45309; font-weight: 600;">⚡ Missing / Recommended:</span>
                                                        <c:forEach var="ms" items="${rec.missingSkills}">
                                                            <span style="display: inline-block; background: #fef3c7; color: #92400e; padding: 2px 6px; border-radius: 4px; margin-right: 4px; font-weight: 500;">${ms}</span>
                                                        </c:forEach>
                                                    </div>
                                                </c:if>
                                            </div>

                                            <div style="text-align: right; margin-top: 0.5rem;">
                                                <c:choose>
                                                    <c:when test="${rec.external}">
                                                        <a href="${rec.actionUrl}" target="_blank" rel="noopener noreferrer" class="btn btn-primary" style="padding: 0.4rem 0.85rem; font-size: 0.85rem;">
                                                            View External Job ↗
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${pageContext.request.contextPath}/${rec.actionUrl}" class="btn btn-primary" style="padding: 0.4rem 0.85rem; font-size: 0.85rem;">
                                                            View & Apply ➔
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div style="text-align: center; padding: 3rem 1rem; color: var(--text-muted);">
                                    <div style="font-size: 3rem; margin-bottom: 0.5rem;">📄</div>
                                    <h4 style="font-weight: 600; font-size: 1.1rem; color: var(--text-main);">No Resume Uploaded Yet</h4>
                                    <p style="font-size: 0.85rem; max-width: 400px; margin: 0.5rem auto 0 auto;">
                                        Upload your PDF or DOCX resume using the form on the left to extract your candidate profile and discover smart job recommendations.
                                    </p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </main>
        </div>
    </div>
</body>
</html>
