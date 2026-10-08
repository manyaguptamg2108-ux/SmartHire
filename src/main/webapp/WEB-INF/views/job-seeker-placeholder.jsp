<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Job Seeker Portal - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="hero-container">
        <div class="card">
            <h1 class="brand-title">SMART HIRE</h1>
            <h2 class="subtitle">Welcome to Job Seeker Portal</h2>
            <p class="tagline">Logged in as: <strong>${sessionScope.userName}</strong> (${sessionScope.userRole})</p>
            <div style="margin-top: 2rem;">
                <a href="${pageContext.request.contextPath}/logout" class="btn-get-started" style="background: linear-gradient(90deg, #ef4444 0%, #f97316 100%);">Logout</a>
            </div>
        </div>
    </div>
</body>
</html>
