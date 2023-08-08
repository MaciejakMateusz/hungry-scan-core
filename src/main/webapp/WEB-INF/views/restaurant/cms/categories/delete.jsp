<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Usuń Kategorię</title>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
</head>
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


</body>
</html>
