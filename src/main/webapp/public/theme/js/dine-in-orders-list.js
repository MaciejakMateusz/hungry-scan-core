import {renderOrdersList} from "./render-orders-list.js";

const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);

function fetchDineInOrders() {
    return fetch(`http://localhost:8082/api/orders`)
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

document.addEventListener("DOMContentLoaded", function () {
    fetchDineInOrders().then(function (orders) {
        renderOrdersList(orders);
    });
});

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/restaurant-order', function (message) {
        const orders = JSON.parse(message.body);
        console.log('Received order: ', orders);
        renderOrdersList(orders);
    });
});

// const dineInOrdersButton = document.querySelector('#show-dineIn');
// const takeAwayOrdersButton = document.querySelector('#show-takeAway');
//
// dineInOrdersButton.addEventListener('click', function (e) {
//     e.preventDefault();
//     const orderRows = document.querySelectorAll('.order-rows');
//     orderRows.forEach(function (orderRow){
//         mainTableBody.removeChild(orderRow);
//     });
//
//     fetchDineInOrders().then(function (orders) {
//         renderOrdersList(orders);
//     });
// });
//
// takeAwayOrdersButton.addEventListener('click', function (e) {
//     e.preventDefault();
//     const orderRows = document.querySelectorAll('.order-rows');
//     orderRows.forEach(function (orderRow){
//         mainTableBody.removeChild(orderRow);
//     });
//
//     fetchTakeAwayOrders().then(function (orders) {
//         renderOrdersList(orders);
//     });
// });