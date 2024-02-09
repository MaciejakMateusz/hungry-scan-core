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
                <div class="main-button" id="categories-management">
                    <span class="black-bold">Zarządzanie kategoriami</span>
                </div>
                <div class="main-button selected-button" id="restaurants-management">
                    <span class="black-bold">Zarządzanie restauracjami</span>
                </div>
            </div>
        </div>

        <div class="content cms-content">
            <div class="tables-box fit-content unset-overflow-y" id="tables-box">
                <div class="container-menu cms-form">
                    <div class="grid-container-category cms-form">
                        <div class="cms-form-title-area">
                            <span>Dodaj nową restaurację</span>
                        </div>
                        <form:form method="POST"
                                   action="/restaurant/cms/restaurants/add"
                                   modelAttribute="restaurant"
                                   cssClass="category-form-grid">
                            <div class="category-name-input-area">
                                <label for="name" class="nam-input-definition-area"> Nazwa:</label>
                                <form:input path="name"
                                            maxlength="100"
                                            cssClass="nam-input-field-area"/>
                                <form:errors path="name"
                                             cssClass="validation nam-input-field-area nam-validation-pos"/>
                            </div>

                            <div class="category-description-input-area">
                                <label for="address" class="des-input-definition-area">Adres:</label>
                                <form:textarea path="address"
                                               maxlength="400"
                                               cssClass="des-input-field-area cat-desc-height"/>
                                <form:errors path="address"
                                             cssClass="validation des-input-field-area cat-des-validation-pos"/>
                            </div>
                            <div class="form-action-buttons-area">
                                <button type="submit" class="cms-action-button cms-edit">Dodaj restaurację</button>
                                <div class="cms-action-button cms-delete" id="cancel-restaurant-action-button">Anuluj</div>
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
