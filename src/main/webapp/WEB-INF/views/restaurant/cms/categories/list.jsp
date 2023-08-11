<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
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
<div style="padding: 2rem"></div>
<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>
