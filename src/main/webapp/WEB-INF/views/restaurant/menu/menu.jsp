<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">

<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>
<h1>Restaurant menu</h1>

<a href="${pageContext.request.contextPath}/restaurant">
    <button class="btn-primary">Powrót</button>
</a>

<c:forEach items="${categories}" var="category">
    <h2 style="text-align: center">${category.name}</h2>
    <p style="text-align: center">${category.description}</p>
    <c:forEach items="${category.menuItems}" var="menuItem">
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
            </div>
            <c:if test="${menuItem.image!=null}">
                <div style="text-align: left">
                    <img src="data:image/jpeg;base64,${menuItem.base64Image}" alt="${menuItem.name}"/>
                </div>
            </c:if>
        </div>
    </c:forEach>
</c:forEach>
<div style="padding: 2rem"></div>
<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>