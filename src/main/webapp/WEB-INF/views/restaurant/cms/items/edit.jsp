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
            <div class="tables-box fit-content unset-overflow-y" id="tables-box">
                <div class="container-menu cms-form">
                    <div class="grid-container-category cms-form">
                        <div class="cms-form-title-area">
                            <span>Edytuj danie</span>
                        </div>
                        <form:form method="POST"
                                   action="/restaurant/cms/items/update"
                                   modelAttribute="menuItem"
                                   enctype="multipart/form-data"
                                   cssClass="menu-item-form-area">
                            <form:hidden path="id"/>
                            <form:hidden path="created"/>
                            <div class="category-input-area">
                                <label for="category" class="cat-input-definition-area"> Kategoria:</label>
                                <div class="cat-input-field-area custom-select">
                                    <form:select path="category"
                                                 items="${categories}"
                                                 itemValue="id"
                                                 itemLabel="name"/>
                                </div>
                            </div>

                            <div class="name-input-area">
                                <label for="name" class="nam-input-definition-area"> Nazwa:</label>
                                <form:input path="name"
                                            maxlength="200"
                                            cssClass="nam-input-field-area"/>
                                <form:errors path="name"
                                             cssClass="validation nam-input-field-area nam-validation-pos"/>
                            </div>

                            <div class="description-input-area">
                                <label for="description" class="des-input-definition-area"> Opis:</label>
                                <form:textarea path="description"
                                               maxlength="500"
                                               cssClass="des-input-field-area"/>
                                <form:errors path="description"
                                             cssClass="validation des-input-field-area des-validation-pos"/>
                            </div>

                            <div class="price-input-area">
                                <label for="price" class="pri-input-definition-area"> Cena:</label>
                                <form:input type="number"
                                            step="0.01"
                                            path="price"
                                            max="10000"
                                            cssClass="pri-input-field-area"/>
                                <form:errors path="price"
                                             cssClass="validation pri-input-field-area pri-validation-pos"/>                            </div>

                            <div class="photo-input-area">
                                <label for="imageFile" class="pho-input-definition-area"> Zdjęcie:</label>
                                <form:input type="file"
                                            path="imageFile"
                                            accept=".png, .jpg"
                                            cssClass="pho-input-field-area"/>
                            </div>
                            <div class="form-action-buttons-area">
                                <button type="submit" class="cms-action-button cms-edit">Zapisz</button>
                                <div class="cms-action-button cms-delete" id="cancel-item-action-button">Anuluj</div>
                            </div>
                            <div class="available-buttons-area">
                                <span>Dostępność:</span>
                                <label for="available" class="cms-action-button gray-style" id="available-label">
                                    Dostępne
                                    <form:radiobutton path="available"
                                                      id="available"
                                                      class="custom-radio"
                                                      value="true"/>
                                </label>
                                <label for="unavailable" class="cms-action-button gray-style" id="unavailable-label">
                                    Niedostępne
                                    <form:radiobutton path="available"
                                                      id="unavailable"
                                                      class="custom-radio"
                                                      value="false"/>
                                </label>
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
