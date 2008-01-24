<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:app="urn:jsptagdir:/WEB-INF/tags">
	<div id="item_identification">
		Process definition <span id="processdefinitionid" class="id">${model.processDefinitionBean.id}</span>
	</div>
	<div id="item_instances">
		<app:unorderedlist list="${model.processDefinitionBean.processInstanceBeanList}" itemClass="processinstance">
			<jsp:attribute name="listItem">
				<app:processinstance item="${item}" />
			</jsp:attribute>
		</app:unorderedlist>	
	</div>
	<c:if test="${model.canCreate}">
		<div id="item_create">
			<p>Create:</p>
			<form method="post" action="${root}/processdefinition/${model.processDefinitionBean.id}">
				<input type="submit" value="submit" />
			</form>
		</div>
	</c:if>
</jsp:root>