<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ attribute name="item" required="true" type="gov.loc.repository.console.workflow.beans.TaskBean" %>
<span class="task"><span class="name">${item.name}</span></span>