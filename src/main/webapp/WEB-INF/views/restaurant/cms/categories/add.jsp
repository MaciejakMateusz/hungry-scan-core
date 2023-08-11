<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="pl">

<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->

<body>
<h2>Dodaj nową kategorię</h2>

<form:form method="POST"
           action="/restaurant/cms/categories/add"
           modelAttribute="category">
    <div>
        <label for="name"> Nazwa:
            <form:input path="name"/>
        </label>
        <form:errors path="name" cssClass="validation"/>
    </div>
    <div>
        <label for="description"> Opis(opcjonalnie):
            <form:input path="description"/>
        </label>
        <form:errors path="description" cssClass="validation"/>
    </div>
    <div>
        <button type="submit">Dodaj kategorię</button>
    </div>
</form:form>

<a href="${pageContext.request.contextPath}/restaurant/cms/categories"><button>Powrót</button></a>

<div style="padding: 2rem"></div>
<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->

</body>
</html>
