<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.profileSuccessMessage}">
                    <div class="alert alert-success alert-auto-dismiss">
                        <span>✓</span> ${sessionScope.profileSuccessMessage}
                    </div>
                    <% session.removeAttribute("profileSuccessMessage"); %>
                </c:if>

                <div class="grid-cols-3">
                    <!-- Summary Card -->
                    <div class="card">
                        <div style="text-align: center; padding-bottom: 1rem; border-bottom: 1px solid var(--border-color); margin-bottom: 1rem;">
                            <div class="user-avatar" style="width: 80px; height: 80px; font-size: 2rem; margin: 0 auto 1rem auto;">
                                ${jobSeeker.name.substring(0, 1).toUpperCase()}
                            </div>
                            <h3 style="font-weight: 700; font-size: 1.25rem;">${jobSeeker.name}</h3>
                            <p style="font-size: 0.85rem; color: var(--text-muted);">${userAccount.email}</p>
                            <span class="badge badge-primary" style="margin-top: 0.5rem;">Job Seeker</span>
                        </div>

                        <div>
                            <h4 style="font-weight: 700; font-size: 0.9rem; margin-bottom: 0.5rem;">Profile Strength</h4>
                            <div style="display: flex; justify-content: space-between; font-size: 0.85rem; font-weight: 600;">
                                <span>Completion Status</span>
                                <span>${profileCompletion}%</span>
                            </div>
                            <div class="progress-bar-container">
                                <div class="progress-bar-fill" style="width: ${profileCompletion}%;"></div>
                            </div>
                        </div>
                    </div>

                    <!-- Profile Edit Form -->
                    <div class="card" style="grid-column: span 2;">
                        <div class="card-header">
                            <h3 class="card-title">Edit Candidate Information</h3>
                        </div>

                        <form action="${pageContext.request.contextPath}/profile" method="post">
                            <div class="grid-cols-2" style="margin-bottom: 0;">
                                <div class="form-group">
                                    <label class="form-label">Phone Number</label>
                                    <input type="text" name="phone" class="form-control" placeholder="+1-555-0101" value="${jobSeeker.phone}" />
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Location</label>
                                    <input type="text" name="location" class="form-control" placeholder="New York, NY" value="${jobSeeker.location}" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Education History</label>
                                <input type="text" name="education" class="form-control" placeholder="B.S. Computer Science, Stanford University" value="${jobSeeker.education}" />
                            </div>

                            <div class="form-group">
                                <label class="form-label">Experience &amp; Expertise</label>
                                <input type="text" name="experience" class="form-control" placeholder="3 years as Full Stack Java Developer" value="${jobSeeker.experience}" />
                            </div>

                            <div class="form-group">
                                <label class="form-label">Professional Summary / Bio</label>
                                <textarea name="bio" class="form-control" placeholder="Passionate software engineer skilled in Java, Spring Boot, JDBC, and SQL...">${jobSeeker.bio}</textarea>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Resume Document URL / Path</label>
                                <input type="text" name="resumePath" class="form-control" placeholder="https://resumes.smarthire.com/johndoe_resume.pdf" value="${jobSeeker.resumePath}" />
                            </div>

                            <button type="submit" class="btn btn-primary">
                                Save Profile Changes
                            </button>
                        </form>
                    </div>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
