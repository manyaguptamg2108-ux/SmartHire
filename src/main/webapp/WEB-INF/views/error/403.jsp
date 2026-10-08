<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 Access Denied - SmartHire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card empty-state">
            <div class="empty-icon" style="color: var(--danger); font-size: 4rem;">🚫</div>
            <h1 class="empty-title" style="font-size: 2rem;">403 - Access Denied</h1>
            <p class="empty-description">
                You do not have permission to access this resource or administrative area.
            </p>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                Return to Dashboard
            </a>
        </div>
    </div>
</body>
</html>
