<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${job.title} - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.jobDetailSuccessMessage}">
                    <div class="alert alert-success alert-auto-dismiss">
                        <span>✓</span> ${sessionScope.jobDetailSuccessMessage}
                    </div>
                    <% session.removeAttribute("jobDetailSuccessMessage"); %>
                </c:if>

                <c:if test="${not empty sessionScope.jobDetailErrorMessage}">
                    <div class="alert alert-danger alert-auto-dismiss">
                        <span>⚠️</span> ${sessionScope.jobDetailErrorMessage}
                    </div>
                    <% session.removeAttribute("jobDetailErrorMessage"); %>
                </c:if>

                <div class="grid-cols-3">
                    <!-- Main Job Information -->
                    <div class="card" style="grid-column: span 2;">
                        <div style="border-bottom: 1px solid var(--border-color); padding-bottom: 1rem; margin-bottom: 1.5rem;">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                                <div>
                                    <h2 style="font-size: 1.5rem; font-weight: 800;">${job.title}</h2>
                                    <p style="font-size: 1rem; color: var(--primary); font-weight: 600; margin-top: 0.2rem;">
                                        ${not empty company ? company.companyName : 'TechCorp Solutions'}
                                    </p>
                                </div>
                                <span class="badge badge-primary" style="font-size: 0.85rem;">${job.jobType}</span>
                            </div>

                            <div class="job-meta" style="margin-top: 1rem; margin-bottom: 0;">
                                <span>📍 Location: <strong>${job.location}</strong></span>
                                <span>💰 Compensation: <strong>\$${job.salaryMin} - \$${job.salaryMax} / yr</strong></span>
                                <span>📅 Deadline: <strong>${job.applicationDeadline}</strong></span>
                            </div>
                        </div>

                        <div style="margin-bottom: 2rem;">
                            <h3 class="card-title" style="margin-bottom: 0.75rem;">Job Overview &amp; Requirements</h3>
                            <div style="line-height: 1.6; color: var(--text-main); white-space: pre-line;">${job.description}</div>
                        </div>

                        <c:if test="${not empty requiredSkills}">
                            <div style="margin-bottom: 2rem;">
                                <h3 class="card-title" style="margin-bottom: 0.75rem;">Required Technical Skills</h3>
                                <div class="job-skills-list">
                                    <c:forEach var="skill" items="${requiredSkills}">
                                        <span class="skill-pill" style="font-size: 0.85rem; padding: 0.35rem 0.75rem; background: var(--primary-light); color: var(--primary); border: none;">
                                            ⚡ ${skill.skillName}
                                        </span>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <!-- Application Form / Status -->
                        <div style="border-top: 1px solid var(--border-color); padding-top: 1.5rem;">
                            <c:choose>
                                <c:when test="${hasApplied}">
                                    <div class="alert alert-info">
                                        <span>ℹ️</span> You have already submitted an application for this vacancy on <strong>${userApp.appliedAt.toLocalDate()}</strong>. Current Status: <strong style="text-transform: uppercase;">${userApp.status}</strong>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <h3 class="card-title" style="margin-bottom: 1rem;">Apply for this Position</h3>
                                    <form action="${pageContext.request.contextPath}/job-detail" method="post">
                                        <input type="hidden" name="jobId" value="${job.id}" />
                                        <div class="form-group">
                                            <label class="form-label">Cover Letter / Note for Recruiter</label>
                                            <textarea name="coverLetter" class="form-control" placeholder="Explain why you are the ideal candidate for this role..." required></textarea>
                                        </div>
                                        <button type="submit" class="btn btn-primary btn-lg">
                                            🚀 Submit Job Application
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Sidebar Company Info -->
                    <div class="card">
                        <h3 class="card-title" style="margin-bottom: 1rem;">About Company</h3>
                        <c:choose>
                            <c:when test="${not empty company}">
                                <div style="display: flex; flex-direction: column; gap: 0.75rem; font-size: 0.9rem;">
                                    <div>
                                        <strong style="color: var(--text-muted); display: block; font-size: 0.75rem; text-transform: uppercase;">Company Name</strong>
                                        <span style="font-weight: 700; font-size: 1.1rem;">${company.companyName}</span>
                                    </div>
                                    <div>
                                        <strong style="color: var(--text-muted); display: block; font-size: 0.75rem; text-transform: uppercase;">Industry</strong>
                                        <span>${company.industry}</span>
                                    </div>
                                    <div>
                                        <strong style="color: var(--text-muted); display: block; font-size: 0.75rem; text-transform: uppercase;">Headquarters</strong>
                                        <span>📍 ${company.location}</span>
                                    </div>
                                    <c:if test="${not empty company.website}">
                                        <div>
                                            <strong style="color: var(--text-muted); display: block; font-size: 0.75rem; text-transform: uppercase;">Website</strong>
                                            <a href="${company.website}" target="_blank">${company.website}</a>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty company.description}">
                                        <div style="border-top: 1px solid var(--border-color); padding-top: 0.75rem; margin-top: 0.5rem;">
                                            <p style="color: var(--text-muted); font-size: 0.85rem;">${company.description}</p>
                                        </div>
                                    </c:if>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p style="color: var(--text-muted); font-size: 0.9rem;">Company details available upon recruiter profile verification.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
