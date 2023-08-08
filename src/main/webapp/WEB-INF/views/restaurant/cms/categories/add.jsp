<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Dodaj kategorię</title>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
</head>
<body>

<h2>Dodaj nową kategorię</h2>

<form:form method="POST"
           action="/restaurant/cms/categories/add"
           modelAttribute="category">
    <div>
        <label for="name"> Nazwa:
            <form:input path="name"/>
        </label>
    </div>
    <div>
        <label for="description"> Opis(opcjonalnie):
            <form:input path="description"/>
        </label>
    </div>
    <div>
        <button type="submit">Dodaj kategorię</button>
    </div>
</form:form>

<a href="${pageContext.request.contextPath}/restaurant/cms/categories"><button>Powrót</button></a>

</body>
</html>
