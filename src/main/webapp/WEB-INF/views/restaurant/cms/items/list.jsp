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

        <div class="content cms-content">
            <div class="tables-box fit-content" id="tables-box">
                <div class="container-menu">
                    <c:forEach items="${categories}" var="category">
                        <div class="grid-container-category">
                            <div class="category">
                                <span>${category.name}</span>
                            </div>
                            <div class="cms-menu-items">
                                <c:forEach items="${category.menuItems}" var="menuItem">
                                    <div class="cms-menu-item">
                                        <div class="menu-item-area-cms">
                                            <span class="cms-item-title-area">${menuItem.name}</span>
                                            <span class="cms-item-description-area">${menuItem.description}</span>
                                            <div class="cms-item-price-area">
                                                <div class="increment-button"></div>
                                                <span>${menuItem.price} zł</span>
                                            </div>
                                            <div class="cms-item-photo-area">
                                                <c:if test="${menuItem.base64Image!=null}">
                                                    <c:if test="${menuItem.image!=null}">
                                                        <div class="cms-photo-container">
                                                            <img src="data:image/jpeg;base64,${menuItem.base64Image}"
                                                                 alt="${menuItem.name}"
                                                                 class="menu-item-img"/>
                                                        </div>
                                                    </c:if>
                                                </c:if>
                                            </div>
                                        </div>
                                        <div class="menu-item-available-area">
                                            <c:choose>
                                                <c:when test="${menuItem.available}">
                                                    <span>Dostępne</span>
                                                    <span class="check-icon available-icon-list-position"></span>
                                                </c:when>
                                                <c:when test="${!menuItem.available}">
                                                    <span>Niedostępne</span>
                                                    <span class="x-icon unavailable-icon-list-position"></span>
                                                </c:when>
                                            </c:choose>
                                        </div>
                                        <div class="menu-item-action-buttons-area">
                                            <form action="${pageContext.request.contextPath}/restaurant/cms/items/edit"
                                                  method="POST"
                                                  style="display: inline-block">
                                                <input type="hidden" name="id" value="${menuItem.id}"/>
                                                <button type="submit" class="cms-action-button cms-edit">Edytuj danie</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/restaurant/cms/items/delete"
                                                  method="POST"
                                                  style="display: inline-block">
                                                <input type="hidden" name="id" value="${menuItem.id}"/>
                                                <button type="submit" class="cms-action-button cms-delete">Usuń danie</button>
                                            </form>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="bottom-buttons-area">
            <div class="add-item-area">
                <div class="add-item-button" id="add-item-button">
                    <div class="add-item-icon"></div>
                    <span>Nowe danie</span>
                </div>
            </div>
            <div class="back-to-main-view-area">
                <div class="back-button" id="back-button">
                    <span>Powrót do panelu restauracji</span>
                    <span class="back-icon"></span>
                </div>
            </div>
        </div>

    </div>
</div>
<script src="<c:url value="/webjars/sockjs-client/1.5.1/sockjs.min.js"/>"></script>
<script src="<c:url value="/webjars/stomp-websocket/2.3.4/stomp.min.js"/>"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/cms.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/main-menu-buttons-redirects.js"></script>
</body>
</html>
