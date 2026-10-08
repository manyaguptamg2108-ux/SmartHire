<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Posted Jobs - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.myJobsSuccessMessage}">
                    <div class="alert alert-success alert-auto-dismiss">
                        <span>✓</span> ${sessionScope.myJobsSuccessMessage}
                    </div>
                    <% session.removeAttribute("myJobsSuccessMessage"); %>
                </c:if>

                <c:if test="${not empty sessionScope.myJobsErrorMessage}">
                    <div class="alert alert-danger alert-auto-dismiss">
                        <span>⚠️</span> ${sessionScope.myJobsErrorMessage}
                    </div>
                    <% session.removeAttribute("myJobsErrorMessage"); %>
                </c:if>

                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Manage Posted Vacancies</h3>
                        <a href="${pageContext.request.contextPath}/post-job" class="btn btn-sm btn-primary">➕ Post New Job</a>
                    </div>

                    <c:choose>
                        <c:when test="${not empty jobs}">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Title</th>
                                            <th>Type</th>
                                            <th>Location</th>
                                            <th>Salary Range</th>
                                            <th>Deadline</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="job" items="${jobs}">
                                            <tr>
                                                <td>#${job.id}</td>
                                                <td>
                                                    <strong><a href="${pageContext.request.contextPath}/job-detail?id=${job.id}">${job.title}</a></strong>
                                                </td>
                                                <td><span class="badge badge-info">${job.jobType}</span></td>
                                                <td>📍 ${job.location}</td>
                                                <td>💰 \$${job.salaryMin} - \$${job.salaryMax}</td>
                                                <td>${job.applicationDeadline}</td>
                                                <td>
                                                    <span class="badge badge-${job.status == 'ACTIVE' ? 'success' : 'secondary'}">
                                                        ${job.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <div style="display: flex; gap: 0.4rem;">
                                                        <a href="${pageContext.request.contextPath}/applicants?jobId=${job.id}" class="btn btn-sm btn-outline">
                                                            Applicants
                                                        </a>
                                                        <c:if test="${job.status == 'ACTIVE'}">
                                                            <form action="${pageContext.request.contextPath}/my-jobs" method="post" style="display: inline;">
                                                                <input type="hidden" name="jobId" value="${job.id}" />
                                                                <input type="hidden" name="action" value="close" />
                                                                <button type="submit" class="btn btn-sm btn-secondary" data-confirm="Close this job posting?">Close</button>
                                                            </form>
                                                        </c:if>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">📋</div>
                                <h3 class="empty-title">No Posted Jobs</h3>
                                <p class="empty-description">You haven't posted any job vacancies yet. Create your first listing to start accepting candidates.</p>
                                <a href="${pageContext.request.contextPath}/post-job" class="btn btn-primary">Post a Job</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
