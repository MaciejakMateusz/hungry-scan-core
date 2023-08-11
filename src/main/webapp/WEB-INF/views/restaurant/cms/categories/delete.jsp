<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>

<h2>Usuwanie kategorii</h2>
<h4>Potwierdź usunięcie tej kategorii:</h4>

<p>ID: ${category.id}</p>
<p>Nazwa: ${category.name}</p>
<c:if test="${category.description!=null}">
    <p>Opis: ${category.description}</p>
</c:if>

<div>
    <form:form
            action="/restaurant/cms/categories/remove"
            method="post"
            modelAttribute="category"
            cssStyle="display: inline-block">
        <form:hidden path="id"/>
        <button>Usuń tą kategorię</button>
    </form:form>
    <a href="${pageContext.request.contextPath}/restaurant/cms/categories"><button>Powrót</button></a>
</div>
<div style="padding: 2rem"></div>
<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>
