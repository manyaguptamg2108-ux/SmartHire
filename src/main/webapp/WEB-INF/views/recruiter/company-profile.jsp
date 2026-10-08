<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Profile - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <c:if test="${not empty sessionScope.companySuccessMessage}">
                    <div class="alert alert-success alert-auto-dismiss">
                        <span>✓</span> ${sessionScope.companySuccessMessage}
                    </div>
                    <% session.removeAttribute("companySuccessMessage"); %>
                </c:if>

                <c:if test="${not empty sessionScope.companyErrorMessage}">
                    <div class="alert alert-danger alert-auto-dismiss">
                        <span>⚠️</span> ${sessionScope.companyErrorMessage}
                    </div>
                    <% session.removeAttribute("companyErrorMessage"); %>
                </c:if>

                <div class="grid-cols-3">
                    <div class="card">
                        <div style="text-align: center; padding-bottom: 1rem; border-bottom: 1px solid var(--border-color); margin-bottom: 1rem;">
                            <div class="metric-icon teal" style="width: 80px; height: 80px; font-size: 2.5rem; margin: 0 auto 1rem auto;">
                                🏢
                            </div>
                            <h3 style="font-weight: 700; font-size: 1.25rem;">${not empty company ? company.companyName : 'New Company'}</h3>
                            <p style="font-size: 0.85rem; color: var(--text-muted);">${not empty company ? company.industry : 'Industry N/A'}</p>
                            <span class="badge badge-info" style="margin-top: 0.5rem;">Recruiter Enterprise Account</span>
                        </div>
                    </div>

                    <div class="card" style="grid-column: span 2;">
                        <div class="card-header">
                            <h3 class="card-title">Company Profile Details</h3>
                        </div>

                        <form action="${pageContext.request.contextPath}/company-profile" method="post">
                            <div class="grid-cols-2" style="margin-bottom: 0;">
                                <div class="form-group">
                                    <label class="form-label">Company Name</label>
                                    <input type="text" name="companyName" class="form-control" placeholder="TechCorp Solutions Inc." value="${company.companyName}" required />
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Industry</label>
                                    <input type="text" name="industry" class="form-control" placeholder="Information Technology &amp; Services" value="${company.industry}" required />
                                </div>
                            </div>

                            <div class="grid-cols-2" style="margin-bottom: 0;">
                                <div class="form-group">
                                    <label class="form-label">Headquarters Location</label>
                                    <input type="text" name="location" class="form-control" placeholder="San Francisco, CA" value="${company.location}" required />
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Company Website URL</label>
                                    <input type="text" name="website" class="form-control" placeholder="https://www.techcorp.com" value="${company.website}" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Company Overview / Mission</label>
                                <textarea name="description" class="form-control" placeholder="Describe your company, culture, values, and core products...">${company.description}</textarea>
                            </div>

                            <button type="submit" class="btn btn-primary">
                                Save Company Profile
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
