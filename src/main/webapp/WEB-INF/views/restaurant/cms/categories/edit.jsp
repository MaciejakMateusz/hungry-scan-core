<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>

<h2>Edytuj kategorię</h2>

<form:form method="POST"
           action="/restaurant/cms/categories/update"
           modelAttribute="category">
    <form:hidden path="id"/>
    <form:hidden path="created"/>
    <div>
        <label for="name"> Nazwa:
            <form:input path="name"/>
        </label>
        <form:errors path="name" cssClass="validation"/>
    </div>
    <div>
        <label for="description"> Opis(opcjonalnie):
            <form:textarea path="description"/>
        </label>
        <form:errors path="description" cssClass="validation"/>
    </div>
    <div>
        <button type="submit" class="btn-primary">Edytuj kategorię</button>
    </div>
</form:form>

<a href="${pageContext.request.contextPath}/restaurant/cms/categories"><button class="btn-primary">Powrót</button></a>
<div style="padding: 2rem"></div>
<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>