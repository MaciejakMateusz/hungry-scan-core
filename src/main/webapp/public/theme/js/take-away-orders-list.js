import {renderOrdersList} from "./render-orders-list.js";

const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);

function fetchTakeAwayOrders() {
    return fetch(`http://localhost:8082/api/orders/takeAway`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/takeAway-orders', function (message) {
        const orders = JSON.parse(message.body);
        console.log('Received order: ', orders);
        renderOrdersList(orders);
    });
});

document.addEventListener("DOMContentLoaded", function () {
    fetchTakeAwayOrders().then(function (orders) {
        renderOrdersList(orders);
    });
});