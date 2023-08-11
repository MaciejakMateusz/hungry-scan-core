<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>
<h1>Główny widok restauracji</h1>
<c:if test="${user.admin==true}">
    <div>
        <a href="${pageContext.request.contextPath}/restaurant/cms"><button>CMS</button></a>
    </div>
</c:if>

<div>
    <a href="${pageContext.request.contextPath}/restaurant/menu"><button>Menu</button></a>
</div>

<div>
    <a href="${pageContext.request.contextPath}/restaurant/logout"><button>Wyloguj się</button></a>
</div>

<h3>Stoliki i inne funkcjonalności</h3>

<div style="padding: 2rem"></div>

<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>