<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Lista Dań</title>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
</head>
<body>

<a href="${pageContext.request.contextPath}/restaurant/cms">
    <button>Powrót do menu głównego</button>
</a>

<h2>Lista dań</h2>

<a href="${pageContext.request.contextPath}/restaurant/cms/items/add">
    <button>Dodaj danie</button>
</a>

<c:forEach items="${menuItems}" var="menuItem">
    <p>ID: ${menuItem.id}</p>
    <p>Nazwa: ${menuItem.name}</p>
    <p>Opis: ${menuItem.description}</p>
    <p>Składniki: ${menuItem.ingredients}</p>
    <p>Kategoria: ${menuItem.category}</p>
    <p>Cena: ${menuItem.price}zł</p>
    <c:choose>
        <c:when test="${menuItem.available==true}">
            <p>Dostępny: tak</p>
        </c:when>
        <c:when test="${menuItem.available==false}">
            <p>Dostępny: nie</p>
        </c:when>
    </c:choose>
    <c:if test="${menuItem.image!=null}">
        <p>Zdjęcie:</p>
        <div>
            <img src="data:image/jpeg;base64,${menuItem.base64Image}" alt="${menuItem.name}"/>
        </div>
    </c:if>
    <form action="${pageContext.request.contextPath}/restaurant/cms/items/edit"
          method="POST"
          style="display: inline-block">
        <input type="hidden" name="id" value="${menuItem.id}"/>
        <button type="submit">Edytuj danie</button>
    </form>
    <form action="${pageContext.request.contextPath}/restaurant/cms/items/delete"
          method="POST"
          style="display: inline-block">
        <input type="hidden" name="id" value="${menuItem.id}"/>
        <button type="submit">Usuń danie</button>
    </form>
</c:forEach>

</body>
</html>
