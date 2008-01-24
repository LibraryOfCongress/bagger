<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:tiles="http://tiles.apache.org/tags-tiles"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:app="urn:jsptagdir:/WEB-INF/tags">

	<div id="variables">
		<p>Variables:</p>
		<c:forEach items="${model.taskInstanceBean.taskBean.variableBeanList}" var="variableBean">
			<c:choose>
				<c:when test="${variableBean.writable}">
					<c:choose>
						<c:when test="${variableBean.required}">
							<c:set var="variableClass" value="variable writable required" />
						</c:when>
						<c:otherwise>
							<c:set var="variableClass" value="variable writable" />						
						</c:otherwise>
					</c:choose>
					<span class="${variableClass}">
						<span class="name">${variableBean.name}</span>: <input type="text" name="variable.${variableBean.name}" value="${model.taskInstanceBean.variableMap[variableBean.name]}" />
						<c:if test="${variableBean.required}">
							&amp;nbsp;&amp;nbsp;(Required)
						</c:if>
						<br />
					</span>
				</c:when>
				<c:otherwise>
					<span class="variable read_only">
						<span class="name">${variableBean.name}</span> = <span class="value">${model.taskInstanceBean.variableMap[variableBean.name]}</span>
					</span>
				</c:otherwise>
			</c:choose>
		</c:forEach>	
	</div>
</jsp:root>