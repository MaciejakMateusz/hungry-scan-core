import {renderOrderDetails, clearOrderDetails} from "./render-order-details.js";
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

            if(order.restaurantTable.id !== 5 &&
                order.restaurantTable.id !== 6 &&
                order.restaurantTable.id !== 11 &&
                order.restaurantTable.id !== 15) {

                restaurantTableIcon.classList.remove('table-default');
                restaurantTableIcon.classList.add('table-ordered');

            } else {
                restaurantTableIcon.classList.remove('s-table-default');
                restaurantTableIcon.classList.add('s-table-ordered');
            }

            restaurantTableIcon.classList.add('activated');
            restaurantTableIcon.firstElementChild.classList.add('activated');

            let restaurantTableButton = document.querySelector(`#l-table-${order.restaurantTable.id}`);
            let orangeOrderedMark = document.createElement('div');
            orangeOrderedMark.classList.add('table-button-ordered');
            restaurantTableButton.appendChild(orangeOrderedMark);
            restaurantTableButton.classList.add('activated');
            const tableButtonChildren = Array.from(restaurantTableButton.children);
            tableButtonChildren.forEach(child => {
                child.classList.add('activated');
            });
        }

        if (!order.isResolved && order.billRequested) {
            let restaurantTableIcon = document.querySelector(`#r-table-${order.restaurantTable.id}`);

            if(order.restaurantTable.id !== 5 &&
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
});
/** ----- END OF UNSELECT THE TABLE AND CLEAR RIGHT PANEL ----- **/
