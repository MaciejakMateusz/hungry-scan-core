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
                    <span class="black-bold">
                        Na wynos
                    </span>
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
            <div class="door-element">
                <span class="info-text">Wejście</span>
            </div>
            <div class="tables-box" id="tables-box">
                <div class="grid-container-tables">
                    <div id="r-table-1" class="table-icon table-default left-side-1 r-table-1">
                        <span class="table-number r-table-1">1</span>
                    </div>
                    <div id="r-table-2" class="table-icon table-default left-side-2 r-table-2">
                        <span class="table-number r-table-2">2</span>
                    </div>
                    <div id="r-table-3" class="table-icon table-default left-side-3 r-table-3">
                        <span class="table-number r-table-3">3</span>
                    </div>
                    <div id="r-table-4" class="table-icon table-default left-side-4 r-table-4">
                        <span class="table-number r-table-4">4</span>
                    </div>
                    <div id="r-table-5" class="s-table-icon s-table-default left-side-5 r-table-5">
                        <span class="table-number r-table-5">5</span>
                    </div>
                    <div id="r-table-6" class="s-table-icon s-table-default middle-row-5 r-table-6">
                        <span class="table-number r-table-6">6</span>
                    </div>
                    <div id="r-table-7" class="table-icon table-default middle-row-4 r-table-7">
                        <span class="table-number r-table-7">7</span>
                    </div>
                    <div id="r-table-8" class="table-icon table-default middle-row-3 r-table-8">
                        <span class="table-number r-table-8">8</span>
                    </div>
                    <div id="r-table-9" class="table-icon table-default middle-row-2 r-table-9">
                        <span class="table-number r-table-9">9</span>
                    </div>
                    <div id="r-table-10" class="table-icon table-default middle-row-1 r-table-10">
                        <span class="table-number r-table-10">10</span>
                    </div>
                    <div id="r-table-11" class="s-table-icon s-table-default middle-row-10 r-table-11">
                        <span class="table-number r-table-11">11</span>
                    </div>
                    <div id="r-table-12" class="table-icon table-default middle-row-9 r-table-12">
                        <span class="table-number r-table-12">12</span>
                    </div>
                    <div id="r-table-13" class="table-icon table-default middle-row-8 r-table-13">
                        <span class="table-number r-table-13">13</span>
                    </div>
                    <div id="r-table-14" class="table-icon table-default middle-row-7 r-table-14">
                        <span class="table-number r-table-14">14</span>
                    </div>
                    <div id="r-table-15" class="s-table-icon s-table-default right-side-5 r-table-15">
                        <span class="table-number r-table-15">15</span>
                    </div>
                    <div id="r-table-16" class="table-icon table-default right-side-4 r-table-16">
                        <span class="table-number r-table-16">16</span>
                    </div>
                    <div id="r-table-17" class="table-icon table-default right-side-3 r-table-17">
                        <span class="table-number r-table-17">17</span>
                    </div>
                    <div id="r-table-18" class="table-icon table-default right-side-2 r-table-18">
                        <span class="table-number r-table-18">18</span>
                    </div>
                    <div class="bar-element middle-row-6">
                        <span class="info-text">Kasa</span>
                    </div>
                    <div class="column-element right-side-1">
                    </div>
                    <div class="kitchen-element">
                        <span class="info-text">Kuchnia</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="right-column" id="right-column">
            <div class="table-button-list-container" id="table-list-container">
                <div id="l-table-1" class="table-button-list"><span class="l-table-1">Stolik 1</span></div>
                <div id="l-table-2" class="table-button-list"><span class="l-table-2">Stolik 2</span></div>
                <div id="l-table-3" class="table-button-list"><span class="l-table-3">Stolik 3</span></div>
                <div id="l-table-4" class="table-button-list"><span class="l-table-4">Stolik 4</span></div>
                <div id="l-table-5" class="table-button-list"><span class="l-table-5">Stolik 5</span></div>
                <div id="l-table-6" class="table-button-list"><span class="l-table-6">Stolik 6</span></div>
                <div id="l-table-7" class="table-button-list"><span class="l-table-7">Stolik 7</span></div>
                <div id="l-table-8" class="table-button-list"><span class="l-table-8">Stolik 8</span></div>
                <div id="l-table-9" class="table-button-list"><span class="l-table-9">Stolik 9</span></div>
                <div id="l-table-10" class="table-button-list"><span class="l-table-10">Stolik 10</span></div>
                <div id="l-table-11" class="table-button-list"><span class="l-table-11">Stolik 11</span></div>
                <div id="l-table-12" class="table-button-list"><span class="l-table-12">Stolik 12</span></div>
                <div id="l-table-13" class="table-button-list"><span class="l-table-13">Stolik 13</span></div>
                <div id="l-table-14" class="table-button-list"><span class="l-table-14">Stolik 14</span></div>
                <div id="l-table-15" class="table-button-list"><span class="l-table-15">Stolik 15</span></div>
                <div id="l-table-16" class="table-button-list"><span class="l-table-16">Stolik 16</span></div>
                <div id="l-table-17" class="table-button-list"><span class="l-table-17">Stolik 17</span></div>
                <div id="l-table-18" class="table-button-list"><span class="l-table-18">Stolik 18</span></div>
            </div>
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
                <div id="right-bottom-info-wrapper">
                    <p class="info-container-text" id="free-tables">Wolne: 0</p>
                    <p class="info-container-text" id="occupied-tables">Zajęte: 0</p>
                    <p class="info-container-text" id="booked-tables">Zarezerwowane: 0</p>
                    <div class="logout-button" id="logout-button">
                        <div class="logout-icon"></div>
                    </div>
                    <sec:authorize access="hasRole('ADMIN')">
                        <div class="cms-button" id="cms-button">
                            <div class="cms-icon">CMS</div>
                        </div>
                    </sec:authorize>
                </div>
                <div id="waiter-call-wrapper" class="d-none">
                    <div class="waiter-call-container">
                        <div class="waiter-call-info">
                            <span class="waiter-call-icon"></span>
                            <div class="waiter-text-area">
                                <span>Oczekuje na kelnera</span>
                            </div>
                            <form action="${pageContext.request.contextPath}/restaurant/orders/resolve-call"
                                  method="POST"
                                  class="resolve-call-button"
                                  id="resolve-call-form">
                                <span>Potwierdź</span>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
<script src="<c:url value="/webjars/sockjs-client/1.5.1/sockjs.min.js"/>"></script>
<script src="<c:url value="/webjars/stomp-websocket/2.3.4/stomp.min.js"/>"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/main-view-websocket.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/render-order-details.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/main-menu-buttons-redirects.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/take-away-counter.js"></script>
</body>
</html>
