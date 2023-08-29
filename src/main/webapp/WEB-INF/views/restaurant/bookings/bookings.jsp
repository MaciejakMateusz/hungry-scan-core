<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="pl">

<%@ include file="/WEB-INF/views/restaurant/header.jsp" %>

<body>
<div class="global-container">
    <div class="global-grid-container">

        <div class="date-time">
            <div class="date-time-container-wrapper">
                <div class="date-time-container">
                    <div class="date">
                        <span id="date"></span>
                    </div>
                    <div class="time">
                        <span id="time"></span>
                    </div>
                </div>
            </div>
        </div>

        <div class="menu">
            <div class="main-buttons-container">
                <div class="main-button selected-button" id="main-view-button">
                    <span class="black-bold">Widok sali</span>
                </div>
                <div class="main-button" id="take-away-button">
                    <span class="black-bold">Na wynos</span>
                </div>
                <div class="inactive-main-button" id="reservations-button">
                    <span class="black-bold">Rezerwacje</span>
                </div>
                <div class="main-button" id="menu-button">
                    <span class="black-bold">Menu</span>
                </div>
                <div class="main-button" id="finalized-orders-button">
                    <span class="black-bold">Historia</span>
                </div>
            </div>
        </div>

        <div class="content">
            <div class="tables-box" id="tables-box">
            </div>
        </div>

        <div class="right-column" id="right-column">

            <div class="order-details-panel d-none" id="order-details-panel">
                <div class="grid-container-order-details">
                    <div class="table-number-section">
                        <span id="table-number"></span>
                        <p class="table-number-section-order-time" id="order-time"></p>
                    </div>
                    <div class="order-details-section no-button" id="order-details-section">
                        <div class="ordered-items-section" id="order-details">

                        </div>
                        <div id="ordered-total-amount-section">
                            <div class="ordered-total-amount-section">
                                <div class="total-amount">
                                    <span>Do zapłaty:</span>
                                </div>
                                <div class="total-price">
                                    <span class="total-price-span"></span>
                                </div>
                            </div>
                        </div>
                        <div id="bill-requested-finalize-section" class="d-none">
                            <div class="finalize-order-section">
                                <div class="payment-method">
                                    <span>Płatność:</span>
                                </div>
                                <div class="method">
                                    <span id="p-method"></span>
                                </div>
                                <div class="total-amount">
                                    <span>Do zapłaty:</span>
                                </div>
                                <div class="total-price">
                                    <span class="total-price-span"></span>
                                </div>
                                <form action="${pageContext.request.contextPath}/restaurant/orders/finalize-dineIn"
                                      method="POST"
                                      class="finalize-button"
                                      id="finalize-button">
                                    <span>Wystaw rachunek</span>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="right-column-info-container">
                <p class="info-container-text" id="free-tables">Wolne: 0</p>
                <p class="info-container-text" id="occupied-tables">Zajęte: 0</p>
                <p class="info-container-text">Zarezerwowane: 0</p>
                <div class="logout-button" id="logout-button">
                    <div class="logout-icon"></div>
                </div>
                <sec:authorize access="hasRole('ADMIN')">
                    <div class="cms-button" id="cms-button">
                        <div class="cms-icon"></div>
                    </div>
                </sec:authorize>
            </div>
        </div>

    </div>
</div>
<script src="<c:url value="/webjars/sockjs-client/1.5.1/sockjs.min.js"/>"></script>
<script src="<c:url value="/webjars/stomp-websocket/2.3.4/stomp.min.js"/>"></script>
<script src="${pageContext.request.contextPath}/public/theme/js/main-view-websocket.js"></script>
<script src="${pageContext.request.contextPath}/public/theme/js/main-menu-buttons-redirects.js"></script>
<script src="${pageContext.request.contextPath}/public/theme/js/take-away-counter.js"></script>
</body>
</html>
