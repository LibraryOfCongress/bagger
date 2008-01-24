<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ attribute name="item" required="true" type="gov.loc.repository.console.workflow.beans.TaskInstanceBean" %>
<span class="taskinstance"><a href="${root}/taskinstance/${item.id}"><span class="name">${item.taskBean.name}</span></a> (<span class="id">${item.id}</span>)</span>