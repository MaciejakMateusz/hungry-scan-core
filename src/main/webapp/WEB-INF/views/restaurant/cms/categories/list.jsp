<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Lista Kategorii</title>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
</head>
<body>

<a href="${pageContext.request.contextPath}/restaurant/cms">
    <button>Powrót do menu głównego</button>
</a>

<h2>Lista kategorii</h2>

<a href="${pageContext.request.contextPath}/restaurant/cms/categories/add">
    <button>Dodaj kategorię</button>
</a>

<c:forEach items="${categories}" var="category">
    <p>ID: ${category.id}</p>
    <p>Nazwa: ${category.name}</p>
    <c:if test="${category.description!=null}">
        <p>Opis: ${category.description}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/restaurant/cms/categories/edit"
          method="POST"
          style="display: inline-block">
        <input type="hidden" name="id" value="${category.id}"/>
        <button type="submit">Edytuj kategorię</button>
    </form>
    <form action="${pageContext.request.contextPath}/restaurant/cms/categories/delete"
          method="POST"
          style="display: inline-block">
        <input type="hidden" name="id" value="${category.id}"/>
        <button type="submit">Usuń kategorię</button>
    </form>
</c:forEach>

</body>
</html>
