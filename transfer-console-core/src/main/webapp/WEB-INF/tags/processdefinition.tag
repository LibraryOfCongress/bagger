<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ attribute name="item" required="true" type="gov.loc.repository.console.workflow.beans.ProcessDefinitionBean" %>
<c:set var="name">
	<spring:message code="processdefinitions.${item.id}"/>
</c:set>
<span class="processdefinition"><a href="${root}/processdefinition/${item.id}"><span class="name">${name}</span></a> (<span class="id">${item.id}</span>)</span>