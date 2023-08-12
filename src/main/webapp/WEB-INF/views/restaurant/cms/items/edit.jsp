<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>

<h2>Edytuj danie</h2>

<div style="padding-block: 1rem;">
    <a href="${pageContext.request.contextPath}/restaurant/cms/items">
        <button class="btn-primary">Powrót</button>
    </a>
</div>

<form:form method="POST"
           action="/restaurant/cms/items/update"
           modelAttribute="menuItem"
           enctype="multipart/form-data">
    <form:hidden path="id"/>
    <form:hidden path="created"/>
    <div>
        <label for="name"> Nazwa:
            <form:input path="name"/>
        </label>
        <form:errors path="name" cssClass="validation"/>
    </div>
    <div>
        <label for="description"> Opis:
            <form:input path="description"/>
        </label>
        <form:errors path="description" cssClass="validation"/>
    </div>
    <div>
        <label for="ingredients"> Składniki:
            <form:input path="ingredients"/>
        </label>
    </div>
    <div>
        <label for="price"> Cena:
            <form:input path="price"/>
        </label>
        <form:errors path="price" cssClass="validation"/>
    </div>
    <div>
        <label for="category"> Kategoria:
            <form:radiobuttons path="category" items="${categories}" itemValue="id" itemLabel="name"/>
        </label>
    </div>
    <div>
        <c:if test="${menuItem.image!=null}">
            <div>
                <img src="data:image/png;base64,${menuItem.base64Image}" alt="${menuItem.name}"/>
            </div>
        </c:if>
    </div>
    <div>
        <label for="imageFile"> Zdjęcie:
            <form:input type="file"
                        path="imageFile"
                        accept=".png"/>
        </label>
    </div>
    <div>
        <button type="submit" class="btn-primary">Edytuj danie</button>
    </div>
</form:form>

<div style="padding: 2rem"></div>
<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>