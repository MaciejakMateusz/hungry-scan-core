import {renderOrdersList} from "./render-orders-list.js";
import {fetchDineInOrders} from "./utils.js";

const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);

document.addEventListener("DOMContentLoaded", () =>
    fetchDineInOrders().then(orders => renderOrdersList(orders)));

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/restaurant-orders', function (message) {
        const orders = JSON.parse(message.body);
        renderOrdersList(orders);
    });
});