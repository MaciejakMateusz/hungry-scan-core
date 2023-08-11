<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>

<h2>Usuwanie dania</h2>
<h4>Potwierdź usunięcie tego dania:</h4>

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
<div>
    <form:form
            action="/restaurant/cms/items/remove"
            method="post"
            modelAttribute="menuItem"
            cssStyle="display: inline-block">
        <form:hidden path="id"/>
        <button>Usuń to danie</button>
    </form:form>
    <a href="${pageContext.request.contextPath}/restaurant/cms/items"><button>Powrót</button></a>
</div>
<div style="padding: 2rem"></div>
<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>
