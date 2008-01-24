<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ attribute name="item" required="true" type="gov.loc.repository.console.workflow.beans.ProcessInstanceBean" %>
<c:set var="name">
	<c:choose>
		<c:when test="${item.packageName != null}">${item.processDefinitionBean.id} - ${item.packageName}</c:when>
		<c:otherwise>${item.processDefinitionBean.id}</c:otherwise>
	</c:choose>
</c:set>
<span class="processinstance"><span class="name">${name}</span> (<span class="id">${item.id}</span>)</span>