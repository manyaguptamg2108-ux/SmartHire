<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Settings - SmartHire Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
        
        <div class="app-main">
            <jsp:include page="/WEB-INF/views/common/header.jsp" />
            
            <main class="content-body">
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">System Global Settings</h3>
                    </div>

                    <form action="#" method="get" onsubmit="alert('System configuration saved.'); return false;">
                        <div class="form-group">
                            <label class="form-label">Platform Title</label>
                            <input type="text" class="form-control" value="SmartHire – Intelligent Online Job Portal" readOnly />
                        </div>

                        <div class="form-group">
                            <label class="form-label">Session Timeout (Minutes)</label>
                            <input type="number" class="form-control" value="30" />
                        </div>

                        <div class="form-group">
                            <label class="form-label">Public User Registration Status</label>
                            <select class="form-control">
                                <option value="ENABLED">Enabled (Job Seekers &amp; Recruiters)</option>
                                <option value="DISABLED">Disabled (Maintenance Mode)</option>
                            </select>
                        </div>

                        <button type="submit" class="btn btn-primary">
                            Save Configuration
                        </button>
                    </form>
                </div>
            </main>
            
            <jsp:include page="/WEB-INF/views/common/footer.jsp" />
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
