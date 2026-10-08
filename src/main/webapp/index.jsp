<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="util.AuthUtil" %>
<%
    if (AuthUtil.isLoggedIn(request)) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
    request.getRequestDispatcher("/WEB-INF/views/landing.jsp").forward(request, response);
%>
