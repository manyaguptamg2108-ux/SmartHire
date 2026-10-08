<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="app-header">
    <div class="header-left">
        <button id="mobileSidebarToggle" class="mobile-toggle" aria-label="Toggle navigation">
            ☰
        </button>
        <h1 class="page-title">${not empty pageTitle ? pageTitle : 'Dashboard'}</h1>
    </div>
    <div class="header-right">
        <div class="user-profile-menu">
            <div class="user-avatar">
                <c:choose>
                    <c:when test="${not empty sessionScope.user.name}">
                        ${sessionScope.user.name.substring(0, 1).toUpperCase()}
                    </c:when>
                    <c:when test="${not empty sessionScope.userName}">
                        ${sessionScope.userName.substring(0, 1).toUpperCase()}
                    </c:when>
                    <c:otherwise>U</c:otherwise>
                </c:choose>
            </div>
            <div class="user-info">
                <span class="user-name">${not empty sessionScope.user.name ? sessionScope.user.name : sessionScope.userName}</span>
                <span class="user-role-badge ${not empty sessionScope.user.role ? sessionScope.user.role : sessionScope.userRole}">${not empty sessionScope.user.role ? sessionScope.user.role : sessionScope.userRole}</span>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline" title="Sign out of account">
            Logout
        </a>
    </div>
</header>
