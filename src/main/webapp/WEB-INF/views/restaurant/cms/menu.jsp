<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>
<h1>Rarytas CMS</h1>

<div>
    <a href="${pageContext.request.contextPath}/restaurant"><button>Powrót</button></a>
    <a href="${pageContext.request.contextPath}/restaurant/cms/items"><button>Zarządzanie daniami</button></a>
    <a href="${pageContext.request.contextPath}/restaurant/cms/categories"><button>Zarządzanie kategoriami</button></a>
</div>
<div style="padding: 2rem"></div>
<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>
