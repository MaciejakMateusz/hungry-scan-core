<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="pl">
<!-- Header -->
<%@ include file="/WEB-INF/views/header.jsp" %>
<!-- End of Header -->
<body>

<a href="${pageContext.request.contextPath}/restaurant/cms">
    <button class="btn-primary">Powrót do menu głównego</button>
</a>

<h2>Lista opłaconych zamówień</h2>

<div>
    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Czas zamówienia</th>
            <th>Zamówione pozycje</th>
            <th>Opłacony</th>
            <th>Metoda płatności</th>
            <th>Numer stolika</th>
            <th>Zapłacono</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${orders}" var="order">
            <tr>
                <td>${order.id}</td>
                <td>${order.orderTime}</td>
                <td>
                    <table class="table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nazwa</th>
                            <th>Cena</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${order.orderedItems}" var="orderedItem">
                            <tr>
                                <td>${orderedItem.menuItem.id}</td>
                                <td>${orderedItem.menuItem.name}</td>
                                <td>${orderedItem.menuItem.price}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </td>
                <td>${order.paid}</td>
                <td>${order.paymentMethod}</td>
                <td>${order.restaurantTable.id}</td>
                <td>${order.totalAmount}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

<div style="padding: 2rem"></div>

<!-- Footer -->
<%@ include file="/WEB-INF/views/footer.jsp" %>
<!-- End of Footer -->
</body>
</html>
