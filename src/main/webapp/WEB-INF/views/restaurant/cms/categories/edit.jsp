<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Edytuj Kategorię</title>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
</head>
<body>

<h2>Edytuj kategorię</h2>

<form:form method="POST"
           action="/restaurant/cms/categories/update"
           modelAttribute="category">
    <form:hidden path="id"/>
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
        <button type="submit">Edytuj kategorię</button>
    </div>
</form:form>

<a href="${pageContext.request.contextPath}/restaurant/cms/categories"><button>Powrót</button></a>

</body>
</html>