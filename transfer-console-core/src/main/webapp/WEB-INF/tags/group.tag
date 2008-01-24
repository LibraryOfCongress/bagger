<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ attribute name="item" required="false" type="gov.loc.repository.console.workflow.beans.GroupBean" %>
<span class="group">
	<c:choose>
		<c:when test="${item != null}">
			<span class="id">${item.id}</span>
		</c:when>
		<c:otherwise>
			<span class="id">none</span>
		</c:otherwise>
	</c:choose>
</span>