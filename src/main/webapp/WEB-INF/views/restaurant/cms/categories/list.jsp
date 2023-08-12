<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>
<h2>Lista kategorii</h2>
<a href="${pageContext.request.contextPath}/restaurant/cms">
    <button class="btn-primary">Powrót do menu głównego</button>
</a>
<a href="${pageContext.request.contextPath}/restaurant/cms/categories/add">
    <button class="btn-primary">Dodaj kategorię</button>
</a>

<c:forEach items="${categories}" var="category">
    <div class="card shadow bg-white order">
        <p style="text-align: left">ID: ${category.id}</p>
        <h3>${category.name}</h3>
        <c:if test="${category.description!=null}">
            <h5>${category.description}</h5>
        </c:if>
        <div style="align-content: center">
            <form action="${pageContext.request.contextPath}/restaurant/cms/categories/edit"
                  method="POST"
                  style="display: inline-block">
                <input type="hidden" name="id" value="${category.id}"/>
                <button type="submit" class="btn-primary">Edytuj kategorię</button>
            </form>
            <form action="${pageContext.request.contextPath}/restaurant/cms/categories/delete"
                  method="POST"
                  style="display: inline-block">
                <input type="hidden" name="id" value="${category.id}"/>
                <button type="submit" class="btn-danger">Usuń kategorię</button>
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
