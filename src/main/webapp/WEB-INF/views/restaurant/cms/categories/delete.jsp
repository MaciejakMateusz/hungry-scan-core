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
            </div>
        </div>

        <div class="content cms-content">
            <div class="tables-box fit-content unset-overflow-y" id="tables-box">
                <div class="container-menu cms-form">
                    <div class="cms-form-title-area">
                        <span>Potwierdź usunięcie tej kategorii</span>
                    </div>
                    <div class="delete-category-grid">
                        <div class="id-delete-area">ID:</div>
                        <div class="id-value-area">${category.id}</div>
                        <div class="name-delete-area">Nazwa:</div>
                        <div class="name-value-area">${category.name}</div>
                        <div class="description-delete-area">Opis:</div>
                        <div class="description-value-area">${category.description}</div>
                        <form:form
                                action="/restaurant/cms/categories/remove"
                                method="post"
                                modelAttribute="category"
                                cssClass="form-action-buttons-area">
                            <form:hidden path="id"/>

                            <button type="submit" class="cms-action-button cms-edit">Usuń kategorię</button>
                            <div class="cms-action-button cms-delete" id="cancel-category-action-button">
                                Anuluj
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>

        <div class="bottom-buttons-area">
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
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/select-input-handling.js"></script>
</body>
</html>
