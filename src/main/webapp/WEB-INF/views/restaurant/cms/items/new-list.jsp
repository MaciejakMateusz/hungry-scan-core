<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">

<%@ include file="/WEB-INF/views/restaurant/header.jsp" %>

<body>
<div class="global-container">
    <div class="global-cms-grid-container">

        <div class="date-time">
            <div class="date-time-container-wrapper">
                <div class="date-time-container">
                    <div class="date">
                        <span id="date"></span>
                    </div>
                    <div class="time">
                        <span id="time"></span>
                    </div>
                </div>
            </div>
        </div>

        <div class="menu">
            <div class="main-buttons-container cms-style">
                <div class="main-button selected-button" id="menu-items-management">
                    <span class="black-bold">Zarządzanie daniami</span>
                </div>
                <div class="main-button" id="categories-management">
                    <span class="black-bold">Zarządzanie kategoriami</span>
                </div>
            </div>
        </div>

        <div class="content">
            <div class="tables-box" id="tables-box">
                <div class="grid-container-cms">

                </div>
            </div>
        </div>

    </div>
</div>
<script src="<c:url value="/webjars/sockjs-client/1.5.1/sockjs.min.js"/>"></script>
<script src="<c:url value="/webjars/stomp-websocket/2.3.4/stomp.min.js"/>"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/main-view-websocket.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/render-order-details.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/main-menu-buttons-redirects.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/take-away-counter.js"></script>
</body>
</html>
