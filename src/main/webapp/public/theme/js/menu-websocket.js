import {renderOrderDetails} from "./render-order-details.js";
import {clearOrderDetails} from "./render-order-details.js";
import {fetchOrderByTableNumber} from "./utils.js";
import {updateDateTime} from "./utils.js";
import {fetchOrders} from "./utils.js";

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
function renderOrders(orders) {

    orders.forEach(function (order) {

        if (!order.isResolved && !order.billRequested) {

            let restaurantTableButton = document.querySelector(`#l-table-${order.restaurantTable.id}`);
            let tableButtonMark = document.createElement('div');
            tableButtonMark.classList.add(`l-table-${order.restaurantTable.id}`);

            if(order.waiterCalled) {
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

        if (event.target.id && (event.target.id.startsWith('l-table-'))) {
            tableNumber = parseInt(event.target.id.split('-')[2]);
        } else {
            const classList = event.target.classList;
            for (const className of classList) {
                if (className.startsWith('l-table-')) {
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