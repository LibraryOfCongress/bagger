<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ attribute name="item" required="false" type="gov.loc.repository.console.workflow.beans.UserBean" %>
<%@ attribute name="itemId" required="false" %>
<%@ attribute name="userId" required="false" %>
<c:set var="derivedUserId">
	<c:choose>
		<c:when test="${userId != null}">${userId}</c:when>
		<c:when test="${item != null}">${item.id}</c:when>
		<c:otherwise>none</c:otherwise>
	</c:choose>
</c:set>
<c:choose>
	<c:when test="${itemId != null}">
		<span class="user" id="${itemId}">
	</c:when>
	<c:otherwise>
		<span class="user">	
	</c:otherwise>
</c:choose>	
		<c:choose>
			<c:when test="${derivedUserId != 'none'}">
				<a href="${root}/user/${derivedUserId}"><span class="id">${derivedUserId}</span></a>
			</c:when>
			<c:otherwise>
				<span class="id">none</span>
			</c:otherwise>
		</c:choose>
	</span>		

