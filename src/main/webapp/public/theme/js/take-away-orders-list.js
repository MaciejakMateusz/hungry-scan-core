import {renderOrdersList} from "./render-orders-list.js";
import {updateDateTime} from "./utils.js";
import {clearOrderDetails, renderOrderDetails} from "./render-order-details.js";

export function fetchTakeAwayOrders() {
    return fetch(`http://localhost:8082/api/orders/takeAway`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/takeAway");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

function fetchOrderById(id) {
    return fetch(`http://localhost:8082/api/orders/id/${id}`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/id");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    stompClient.subscribe('/topic/takeAway-orders', function (message) {
        const orders = JSON.parse(message.body);
        renderOrdersList(orders);
    });
});

const ordersListParent = document.querySelector('#orders-list-parent');

document.addEventListener("DOMContentLoaded", function () {
    updateDateTime();
    fetchTakeAwayOrders().then(function (orders) {

        renderOrdersList(orders);
        renderOrderDetails(orders[0])

        if (ordersListParent !== null) {
            ordersListParent.firstElementChild.classList.add('selected-list-element');
        }
    });
});

const observer = new MutationObserver(function (mutationsList) {
    for (const mutation of mutationsList) {
        if (mutation.type === 'childList') {
            mutation.addedNodes.forEach(function (addedNode) {
                if (addedNode.nodeType === Node.ELEMENT_NODE && addedNode.classList.contains('orders-list-table')) {
                    addedNode.addEventListener('click', function (e) {

                        //Remove the class from all elements
                        const orderListTables = document.querySelectorAll('.orders-list-table');
                        orderListTables.forEach(element => {
                            element.classList.remove('selected-list-element');
                        });

                        //Add the class to the clicked element
                        addedNode.classList.add('selected-list-element');

                        let orderId = addedNode.firstElementChild.innerText;
                        fetchOrderById(orderId).then(order => {
                            clearOrderDetails();
                            renderOrderDetails(order);
                        });
                    });
                }
            });
        }
    }
});

if (ordersListParent !== null) {
    observer.observe(ordersListParent, {childList: true});
}
