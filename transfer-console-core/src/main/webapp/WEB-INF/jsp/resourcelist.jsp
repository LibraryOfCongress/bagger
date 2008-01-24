<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:app="urn:jsptagdir:/WEB-INF/tags">

	<spring:message code="root" var="root" />
	<p>resources:</p>
	<ul id="resourcelist">
		<li class="resource"><a href="${root}/user/">user</a></li>
		<li class="resource"><a href="${root}/processdefinition/">processdefinition</a></li>
		<li class="resource">taskinstance</li>		
	</ul>	
</jsp:root>