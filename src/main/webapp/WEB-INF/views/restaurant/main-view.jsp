<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Restaurant main view</title>
</head>
<body>
<h1>Restaurant main view</h1>
<c:if test="${user.admin==true}">
    <div>
        <a href="${pageContext.request.contextPath}/restaurant/cms"><button>CMS</button></a>
    </div>
</c:if>

<a href="${pageContext.request.contextPath}/restaurant/logout"><button>Wyloguj się</button></a>

<div>
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
    </c:forEach>
</div>

</body>
</html>