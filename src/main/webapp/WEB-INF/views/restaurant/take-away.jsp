<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>

<div style="display: inline-block">
    <a href="${pageContext.request.contextPath}/restaurant">
        <button class="btn-primary">Powrót</button>
    </a>
</div>
<div style="display: inline-block">
    <a href="${pageContext.request.contextPath}/restaurant/orders/finalized">
        <button class="btn-primary">Sfinalizowane zamówienia</button>
    </a>
</div>


<h3 style="margin-block: 1rem">Zamówienia:</h3>
<div id="orders-container">
    <table class="table" id="dineIn-table">
        <thead>
        <tr>
            <th width="50%" colspan="4" style="text-align: center">
                <a href="${pageContext.request.contextPath}/restaurant/orders">
                    <button class="btn-primary" id="show-dineIn">
                        Zamówienia w restauracji
                    </button>
                </a>
            </th>
            <th width="50%" colspan="4" style="text-align: center">
                <a href="${pageContext.request.contextPath}/restaurant/orders/take-away">
                    <button class="btn-primary" id="show-takeAway">
                        Zamówienia na wynos
                    </button>
                </a>
            </th>
        </tr>
        </thead>
        <thead>
        <tr>
            <th>ID</th>
            <th>Numer</th>
            <th>Czas zamówienia</th>
            <th>Zamówione pozycje</th>
            <th>Opłacony</th>
            <th>Metoda płatności</th>
            <th>Numer stolika</th>
            <th>Zapłacono</th>
        </tr>
        </thead>
        <tbody id="rendered-body">

        </tbody>
    </table>
</div>

<div style="padding: 2rem"></div>

<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->

<script type="module" src="<c:url value="/webjars/sockjs-client/1.5.1/sockjs.min.js"/>"></script>
<script type="module" src="<c:url value="/webjars/stomp-websocket/2.3.4/stomp.min.js"/>"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/render-orders-list.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/take-away-orders-list.js"></script>

</body>
</html>