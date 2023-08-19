<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>
<h1>Główny widok restauracji</h1>
<sec:authorize access="hasRole('ADMIN')">
    <div style="display: inline-block">
        <a href="${pageContext.request.contextPath}/restaurant/cms">
            <button class="btn-primary">CMS</button>
        </a>
    </div>
</sec:authorize>

<div style="display: inline-block">
    <a href="${pageContext.request.contextPath}/restaurant/menu">
        <button class="btn-primary">Menu</button>
    </a>
</div>

<div style="display: inline-block">
    <a href="${pageContext.request.contextPath}/restaurant/orders">
        <button class="btn-primary">Lista zamówień</button>
    </a>
</div>

<div style="display: inline-block; position: absolute; right: 1rem; top: 1rem;">
    <a href="${pageContext.request.contextPath}/restaurant/logout">
        <button class="btn-secondary">Wyloguj się</button>
    </a>
</div>

<div id="order-container">
</div>

<div style="padding: 2rem"></div>

<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->

<script src="<c:url value="/webjars/sockjs-client/1.5.1/sockjs.min.js"/>"></script>
<script src="<c:url value="/webjars/stomp-websocket/2.3.4/stomp.min.js"/>"></script>
<script src="${pageContext.request.contextPath}/public/theme/js/main-view-websocket.js"></script>

</body>
</html>