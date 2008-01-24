<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:tiles="http://tiles.apache.org/tags-tiles"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:app="urn:jsptagdir:/WEB-INF/tags">
	<div id="item_identification">
		Task Instance <span id="taskinstanceid" class="id">${model.taskInstanceBean.id}</span>
	</div>
	<div id="item_information">
		<p>task: <span id="task"><app:task item="${model.taskInstanceBean.taskBean}" /></span></p>
		<p>user: <app:user item="${model.taskInstanceBean.userBean}" itemId="user" /></p>
		<app:unorderedlist list="${model.taskInstance.groupBeanList}" itemClass="group">
			<jsp:attribute name="listItem">
				<app:group item="${item}" />
			</jsp:attribute>
		</app:unorderedlist>
		<p>has ended: <span id="isEnded">${model.taskInstanceBean.ended}</span></p>
		<c:if test="${model.taskInstanceBean.ended}">
			<p>end date: <span id="endDate">${model.taskInstanceBean.endDate}</span></p>
		</c:if>
	</div>
	<div id="item_update">
		<c:if test="${model.canUpdateUser}">
			<div id="update_user">
				<p>reassign user:</p>
				<form class="put_user" method="post" action="${root}/taskinstance/${model.taskInstanceBean.id}?method=put">
					<select name="user">
						<option value="null">none</option>
						<c:forEach items="${model.userBeanList}" var="userBean">
							<option value="${userBean.id}">${userBean.id}</option>					
						</c:forEach>				
					</select>
					<input type="submit" value="submit"/>
				</form>
			</div>
		</c:if>
		<c:if test="${model.taskFormView != null}">
			<div id="update_taskinstance">
				<p>update task:</p>
				<c:if test="${model.instructionView != null}">
					<p>Instruction:</p>
					<div id="instruction">
						<tiles:insertDefinition name="${model.instructionView}"/>
					</div>
				</c:if>
				<form class="put_taskinstance" method="post" action="${root}/taskinstance/${model.taskInstanceBean.id}?method=put" enctype="multipart/form-data">			
					<tiles:insertDefinition name="${model.taskFormView}"/>
					<p>Transition:</p>
					<select id="transition" name="transition">	
						<option value="null">don't complete task</option>	
						<c:forEach items="${model.taskInstanceBean.taskBean.leavingTransitionList}" var="leavingTransition">
							<spring:message code="transitions.${leavingTransition}" text="${leavingTransition}" var="leavingTransitionName" />
							<option value="${leavingTransition}">${leavingTransitionName}</option>
						</c:forEach>
					</select>							
					<input type="submit" value="submit"/>					
				</form>
			</div>
		</c:if>						
	</div>
</jsp:root>