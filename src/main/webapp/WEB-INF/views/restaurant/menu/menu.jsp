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
                <div class="main-button" id="main-view-button">
                    <span class="black-bold">Widok sali</span>
                </div>
                <div class="main-button" id="take-away-button">
                    <span class="black-bold">Na wynos</span>
                </div>
                <div class="inactive-main-button" id="reservations-button">
                    <span class="black-bold">Rezerwacje</span>
                </div>
                <div class="main-button selected-button" id="menu-button">
                    <span class="black-bold">Menu</span>
                </div>
                <div class="main-button" id="finalized-orders-button">
                    <span class="black-bold">Historia</span>
                </div>
            </div>
        </div>

        <div class="content menu-overflow">
            <div class="tables-box fit-content" id="tables-box">
                <div class="container-menu">
                    <c:forEach items="${categories}" var="category">
                        <div class="grid-container-category">
                            <div class="category">
                                <span>${category.name}</span>
                            </div>
                            <div class="menu-items">
                                <c:forEach items="${category.menuItems}" var="menuItem">
                                    <c:if test="${menuItem.available}">
                                        <div class="menu-item">
                                            <span class="item-name">${menuItem.name}</span>
                                            <div class="item-price-label menu-item-price">
                                                <span>${menuItem.price} zł</span>
                                            </div>
                                            <span class="item-description">${menuItem.description}</span>
                                            <span class="item-id">ID: ${menuItem.id}</span>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>
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
                    <p class="info-container-text">Zarezerwowane: 0</p>
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
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/menu-websocket.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/main-menu-buttons-redirects.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/utils.js"></script>
<script type="module" src="${pageContext.request.contextPath}/public/theme/js/take-away-counter.js"></script>
</body>
</html>
