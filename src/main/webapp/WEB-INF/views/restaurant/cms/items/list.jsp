<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>

<h2>Lista dań</h2>

<a href="${pageContext.request.contextPath}/restaurant/cms">
    <button class="btn-primary">Powrót do menu głównego</button>
</a>
<a href="${pageContext.request.contextPath}/restaurant/cms/items/add">
    <button class="btn-primary">Dodaj danie</button>
</a>

<c:forEach items="${menuItems}" var="menuItem">
    <div class="card order">
        <p style="text-align: left">ID: ${menuItem.id}</p>
        <div class="card bg-gradient-primary shadow order">
            <h2>${menuItem.name}</h2>
            <h5>${menuItem.description}</h5>
        </div>
        <div style="text-align: left; padding-left: 1rem">
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
        </div>
        <div style="text-align: right">
            <form action="${pageContext.request.contextPath}/restaurant/cms/items/edit"
                  method="POST"
                  style="display: inline-block">
                <input type="hidden" name="id" value="${menuItem.id}"/>
                <button type="submit" class="btn-primary">Edytuj danie</button>
            </form>
            <form action="${pageContext.request.contextPath}/restaurant/cms/items/delete"
                  method="POST"
                  style="display: inline-block">
                <input type="hidden" name="id" value="${menuItem.id}"/>
                <button type="submit" class="btn-danger">Usuń danie</button>
            </form>
        </div>
    </div>
</c:forEach>

<div style="padding: 2rem"></div>

<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>
