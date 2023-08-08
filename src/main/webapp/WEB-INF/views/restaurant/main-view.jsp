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

</body>
</html>