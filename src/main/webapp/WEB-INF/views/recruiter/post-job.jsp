<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Post Job - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.postJobErrorMessage}">
                    <div class="alert alert-danger alert-auto-dismiss">
                        <span>⚠️</span> ${sessionScope.postJobErrorMessage}
                    </div>
                    <% session.removeAttribute("postJobErrorMessage"); %>
                </c:if>

                <div class="card" style="max-width: 900px; margin: 0 auto;">
                    <div class="card-header">
                        <h3 class="card-title">Create New Job Opening</h3>
                        <span class="badge badge-info">${not empty company ? company.companyName : 'Setup Company Profile'}</span>
                    </div>

                    <form action="${pageContext.request.contextPath}/post-job" method="post">
                        <div class="grid-cols-2" style="margin-bottom: 0;">
                            <div class="form-group">
                                <label class="form-label">Job Title</label>
                                <input type="text" name="title" class="form-control" placeholder="Senior Java Developer" required />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Job Type</label>
                                <select name="jobType" class="form-control" required>
                                    <option value="FULL_TIME">Full Time</option>
                                    <option value="PART_TIME">Part Time</option>
                                    <option value="CONTRACT">Contract</option>
                                    <option value="INTERNSHIP">Internship</option>
                                    <option value="REMOTE">Remote</option>
                                </select>
                            </div>
                        </div>

                        <div class="grid-cols-3" style="margin-bottom: 0;">
                            <div class="form-group">
                                <label class="form-label">Minimum Salary (\$)</label>
                                <input type="number" name="salaryMin" step="5000" class="form-control" placeholder="80000" required />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Maximum Salary (\$)</label>
                                <input type="number" name="salaryMax" step="5000" class="form-control" placeholder="120000" required />
                            </div>
                            <div class="form-group">
                                <label class="form-label">Location</label>
                                <input type="text" name="location" class="form-control" placeholder="Remote / New York, NY" required />
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Application Deadline</label>
                            <input type="date" name="applicationDeadline" class="form-control" required />
                        </div>

                        <div class="form-group">
                            <label class="form-label">Job Description &amp; Candidate Responsibilities</label>
                            <textarea name="description" class="form-control" style="min-height: 140px;" placeholder="Describe key responsibilities, requirements, and role expectations..." required></textarea>
                        </div>

                        <c:if test="${not empty availableSkills}">
                            <div class="form-group">
                                <label class="form-label">Required Technical Skills</label>
                                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 0.5rem; background: var(--bg-main); padding: 1rem; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                                    <c:forEach var="skill" items="${availableSkills}">
                                        <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; cursor: pointer;">
                                            <input type="checkbox" name="skills" value="${skill.id}" /> ${skill.skillName}
                                        </label>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <div style="display: flex; gap: 1rem; margin-top: 1.5rem;">
                            <button type="submit" class="btn btn-primary btn-lg">
                                Publish Job Opening
                            </button>
                            <a href="${pageContext.request.contextPath}/my-jobs" class="btn btn-secondary btn-lg">Cancel</a>
                        </div>
                    </form>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
