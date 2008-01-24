<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:tiles="http://tiles.apache.org/tags-tiles"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:app="urn:jsptagdir:/WEB-INF/tags">
	<div id="item_identification">
		User <span id="userid" class="id">${model.userBean.id}</span>
	</div>
	<div id="item_information">
		<app:unorderedlist list="${model.userBean.groupBeanList}" itemClass="group">
			<jsp:attribute name="listItem">
				<app:group item="${item}" />
			</jsp:attribute>
		</app:unorderedlist>
		<jsp:include flush="true" page="/taskinstance/?user=${model.userBean.id}&amp;format=xhtmlfragment" />
		<jsp:include flush="true" page="/processdefinition/?user=${model.userBean.id}&amp;format=xhtmlfragment" />
	</div>
</jsp:root>