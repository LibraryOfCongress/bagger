<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
	xmlns:tiles="http://tiles.apache.org/tags-tiles"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:app="urn:jsptagdir:/WEB-INF/tags">
    <jsp:directive.page language="java"
        contentType="application/xhtml+xml; charset=UTF-8" pageEncoding="UTF-8" />
    <jsp:text><![CDATA[<?xml version="1.0" encoding="UTF-8" ?>]]></jsp:text>
    <jsp:text><![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">]]></jsp:text>
<c:if test="${root == null}">
	<spring:message code="root" var="root" />
</c:if>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
	<title><tiles:insertAttribute name="title"/></title>
</head>
<body>
	<tiles:insertAttribute name="body" />
	<c:choose>
		<c:when test="${pageContext.request.remoteUser != null}">
			<div id="login" class="logged_in">
				<p>You are logged in as <app:user userId="${pageContext.request.userPrincipal.name}" itemId="currentuser" />.  <a href="${root}/login/logout.html">Logout</a>.</p>		
			</div>
		</c:when>
		<c:otherwise>
			<div id="login" class="logged_out">		
				<p>You are not logged in.  <a href="${root}/login/login.html">Login</a>.</p>
			</div>
		</c:otherwise>
	</c:choose>	
</body>
</html>
</jsp:root>