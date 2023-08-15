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
    <div style="display: inline-block">
        <a href="${pageContext.request.contextPath}/restaurant/cms">
            <button class="btn-primary">CMS</button>
        </a>
    </div>
</c:if>

<div style="display: inline-block">
    <a href="${pageContext.request.contextPath}/restaurant/menu">
        <button class="btn-primary">Menu</button>
    </a>
</div>

<div style="display: inline-block; position: absolute; right: 1rem; top: 1rem;">
    <a href="${pageContext.request.contextPath}/restaurant/logout">
        <button class="btn-secondary">Wyloguj się</button>
    </a>
</div>

<h3 style="margin-block: 1rem">Zamówienia:</h3>
<div id="order-container">
    <div class="order-body">
        <c:forEach items="${orders}" var="order">
            <div class="card shadow bg-white order" id="order-card">
                <h3>Identyfikator zamówienia: ${order.id}</h3>
                <div class="card shadow">
                    <p>Zamówione pozycje:</p>
                    <c:forEach items="${order.orderedItems}" var="orderedItem">
                        <p>ID: ${orderedItem.menuItem.id}</p>
                        <p>Nazwa: ${orderedItem.menuItem.name}</p>
                        <p>Cena: ${orderedItem.menuItem.price}</p>
                    </c:forEach>
                </div>
                <p>Czas zamówienia: ${order.orderTime}</p>
                <p>Opłacony: ${order.paid}</p>
                <p>Metoda płatności: ${order.paymentMethod}</p>
                <p>Numer stolika: ${order.restaurantTable.id}</p>
                <p>Do zapłaty: ${order.totalAmount}</p>
            </div>
        </c:forEach>
    </div>
</div>

<div style="padding: 2rem"></div>

<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->

<script src="<c:url value="/webjars/sockjs-client/1.5.1/sockjs.min.js"/>"></script>
<script src="<c:url value="/webjars/stomp-websocket/2.3.4/stomp.min.js"/>"></script>
<script src="${pageContext.request.contextPath}/public/theme/js/websocket.js"></script>

</body>
</html>