import {clearOrderDetails, renderOrderDetails} from "./render-order-details.js";
import {fetchOrderByTableNumber, fetchOrders, updateDateTime} from "./utils.js";

/** ---- ON PAGE LOAD ----- **/
document.addEventListener('DOMContentLoaded', function () {
    updateDateTime();
    fetchOrders().then(function (orders) {
        renderOrders(orders);
    });
});
/** ---- END OF ON PAGE LOAD ----- **/

/** ----- WEBSOCKET ----- **/
const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function () {
    stompClient.subscribe('/topic/restaurant-order', function (message) {
        const orders = JSON.parse(message.body);
        renderOrders(orders);
    });
});

/** ----- END OF WEBSOCKET ----- **/

/** ----- RENDERING ORDERS ----- **/
export function renderOrders(orders) {

    orders.forEach(function (order) {

        if (!order.isResolved && !order.billRequested) {
            let restaurantTableIcon = document.querySelector(`#r-table-${order.restaurantTable.id}`);

            if (order.restaurantTable.id !== 5 &&
                order.restaurantTable.id !== 6 &&
                order.restaurantTable.id !== 11 &&
                order.restaurantTable.id !== 15) { //basically if the table is not small, perform those actions:

                restaurantTableIcon.classList.remove('table-default');
                if (order.waiterCalled) {
                    restaurantTableIcon.classList.add('table-waiter-call');
                } else {
                    restaurantTableIcon.classList.add('table-ordered');
                }

            } else { //else if the table is small:
                restaurantTableIcon.classList.remove('s-table-default');
                if (order.waiterCalled) {
                    restaurantTableIcon.classList.add('s-table-waiter-call');
                } else {
                    restaurantTableIcon.classList.add('s-table-ordered');
                }
            }

            restaurantTableIcon.classList.add('activated');
            restaurantTableIcon.firstElementChild.classList.add('activated');

            let restaurantTableButton = document.querySelector(`#l-table-${order.restaurantTable.id}`);
            let tableButtonMark = document.createElement('div');
            tableButtonMark.classList.add(`l-table-${order.restaurantTable.id}`);

            if (order.waiterCalled) {
                tableButtonMark.classList.add('table-button-call');
                restaurantTableButton.appendChild(tableButtonMark);
            } else {
                tableButtonMark.classList.add('table-button-ordered');
                restaurantTableButton.appendChild(tableButtonMark);
            }

            restaurantTableButton.classList.add('activated');
            const tableButtonChildren = Array.from(restaurantTableButton.children);
            tableButtonChildren.forEach(child => {
                child.classList.add('activated');
            });

        }

        if (!order.isResolved && order.billRequested) {
            let restaurantTableIcon = document.querySelector(`#r-table-${order.restaurantTable.id}`);

            if (order.restaurantTable.id !== 5 &&
                order.restaurantTable.id !== 6 &&
                order.restaurantTable.id !== 11 &&
                order.restaurantTable.id !== 15) {

                restaurantTableIcon.classList.remove('table-ordered');
                restaurantTableIcon.classList.add('table-bill-requested');

            } else {
                restaurantTableIcon.classList.remove('s-table-ordered');
                restaurantTableIcon.classList.add('s-table-bill-requested');
            }


            restaurantTableIcon.classList.add('activated');
            restaurantTableIcon.firstElementChild.classList.add('activated');

            let restaurantTableButton = document.querySelector(`#l-table-${order.restaurantTable.id}`);
            let greenBillMark = document.createElement('div');
            greenBillMark.classList.add('table-button-bill');
            greenBillMark.classList.add(`l-table-${order.restaurantTable.id}`);
            restaurantTableButton.appendChild(greenBillMark);
            restaurantTableButton.classList.add('activated');
            const tableButtonChildren = Array.from(restaurantTableButton.children);
            tableButtonChildren.forEach(child => {
                child.classList.add('activated');
            });
        }

    });
}

/** ----- END OF RENDERING ORDERS ----- **/

/** ----- SELECT THE ACTIVE TABLE AND RENDER ORDER DETAILS ----- **/
document.body.addEventListener('click', function (event) {

    if (event.target.classList.contains('activated')) {
        document.querySelector('#table-list-container').classList.add('d-none');
        document.querySelector('#order-details-panel').classList.remove('d-none');

        let tableNumber;

        if (event.target.id && (event.target.id.startsWith('l-table-') || event.target.id.startsWith('r-table-'))) {
            tableNumber = parseInt(event.target.id.split('-')[2]);
        } else {
            const classList = event.target.classList;
            for (const className of classList) {
                if (className.startsWith('l-table-') || className.startsWith('r-table-')) {
                    tableNumber = parseInt(className.split('-')[2]);
                    break;
                }
            }
        }

        if (tableNumber !== undefined) {
            fetchOrderByTableNumber(tableNumber).then(order => {
                clearOrderDetails();
                renderOrderDetails(order);
            });
        }
    }
});
/** ----- END ----- **/

/** ----- UNSELECT THE TABLE AND CLEAR RIGHT PANEL ----- **/
document.querySelector('#tables-box').addEventListener('click', () => {
    document.querySelector('#table-list-container').classList.remove('d-none');
    document.querySelector('#order-details-panel').classList.add('d-none');
    document.querySelector('#waiter-call-wrapper').classList.add('d-none');
    document.querySelector('#right-bottom-info-wrapper').classList.remove('d-none');
});
/** ----- END OF UNSELECT THE TABLE AND CLEAR RIGHT PANEL ----- **/

/** ----- HOVER EVENT LISTENERS FOR TABLES ----- **/
document.body.addEventListener('mouseover', event => {
    const target = event.target;
    const parentWithClass = target.closest('.table-waiter-call, .table-ordered, .table-bill-requested, .s-table-ordered, .s-table-bill-requested, .s-table-waiter-call');

    if (parentWithClass) {
        const selectedIndicatorBox = document.createElement('div');

        if (parentWithClass.classList.contains('table-waiter-call')) {
            selectedIndicatorBox.classList.add('table-call-hovered');
        } else if (parentWithClass.classList.contains('table-ordered') || parentWithClass.classList.contains('table-bill-requested')) {
            selectedIndicatorBox.classList.add('table-hovered');
        } else if (parentWithClass.classList.contains('s-table-ordered') || parentWithClass.classList.contains('s-table-bill-requested')) {
            selectedIndicatorBox.classList.add('s-table-hovered');
        } else if (parentWithClass.classList.contains('s-table-waiter-call')) {
            selectedIndicatorBox.classList.add('s-table-call-hovered');
        }

        parentWithClass.appendChild(selectedIndicatorBox);

        // Add a mouseout event listener to remove the indicator box
        parentWithClass.addEventListener('mouseout', () => {
            selectedIndicatorBox.remove();
        });
    }
});
/** ----- END OF HOVER EVENT LISTENERS FOR TABLES ----- **/