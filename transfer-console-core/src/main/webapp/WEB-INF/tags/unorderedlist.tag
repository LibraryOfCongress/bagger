<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="list" required="true" type="java.util.List" %>
<%@ attribute name="itemClass" required="true" %>
<%@ attribute name="listId" required="false" %>
<%@ attribute name="listLabel" required="false" %>
<%@ attribute name="listItem" required="true" fragment="true" %>
<%@ variable name-given="item" variable-class="java.lang.Object" %>
<c:set var="derivedListId">
	<c:choose>
		<c:when test="${listId != null}">${listId}</c:when>
		<c:otherwise>${itemClass}_list</c:otherwise>
	</c:choose>	
</c:set>
<c:set var="derivedListLabel">
	<c:choose>
		<c:when test="${listLabel != null}">${listLabel}</c:when>
		<c:otherwise>${itemClass} list</c:otherwise>
	</c:choose>	
</c:set>
<p>${derivedListLabel}</p>
<ul id="${derivedListId}">
	<c:forEach items="${list}" var="item">
		<li class="${itemClass}"><jsp:invoke fragment="listItem" /></li>
	</c:forEach>
</ul>
