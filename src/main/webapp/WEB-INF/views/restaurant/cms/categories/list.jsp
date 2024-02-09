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
                <div class="main-button" id="menu-items-management">
                    <span class="black-bold">Zarządzanie daniami</span>
                </div>
                <div class="main-button selected-button" id="categories-management">
                    <span class="black-bold">Zarządzanie kategoriami</span>
                </div>
                <div class="main-button" id="restaurants-management">
                    <span class="black-bold">Zarządzanie restauracjami</span>
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
                            <div class="cms-menu-category">
                                <div class="cms-category-description-area">
                                    <span>${category.description}</span>
                                </div>
                                <div class="cms-category-action-buttons-area">
                                    <div class="cat-action-buttons-pill">
                                        <form action="${pageContext.request.contextPath}/restaurant/cms/categories/edit"
                                              method="POST"
                                              class="reset-margin-block">
                                            <input type="hidden" name="id" value="${category.id}"/>
                                            <button type="submit" class="button-reset-style">
                                                <span class="edit-icon"></span>
                                            </button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/restaurant/cms/categories/delete"
                                              method="POST"
                                              class="reset-margin-block">
                                            <input type="hidden" name="id" value="${category.id}"/>
                                            <button type="submit" class="button-reset-style">
                                                <span class="trash-icon"></span>
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="bottom-buttons-area">
            <div class="add-item-area">
                <div class="add-item-button" id="add-category-button">
                    <div class="add-item-icon"></div>
                    <span>Nowa kategoria</span>
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
