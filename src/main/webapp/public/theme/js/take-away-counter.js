import {fetchTakeAwayOrders} from "./take-away-orders-list.js";
import {fetchOrders} from "./utils.js";

function countTakeAway(orders) {
    if (orders.length !== 0) {
        const takeAwayCounterBadge = document.createElement('span');
        takeAwayCounterBadge.id = 'take-away-counter-badge';
        takeAwayCounterBadge.innerHTML = orders.length;

        const takeAwayButton = document.querySelector('#take-away-button');
        takeAwayButton.appendChild(takeAwayCounterBadge);
    }
}

function renderTakeAwayCounterBadge() {
    fetchTakeAwayOrders().then(orders => {
        countTakeAway(orders);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    renderTakeAwayCounterBadge();

    fetchOrders().then(orders => {
        countOccupiedTables(orders);
    });
});

/** ----- WEBSOCKET ----- **/
const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function () {
    stompClient.subscribe('/topic/takeAway-orders', function (message) {
        const orders = JSON.parse(message.body);
        countTakeAway(orders);
    });
    stompClient.subscribe('/topic/restaurant-order', function (message) {
        const orders = JSON.parse(message.body);
        countOccupiedTables(orders);
    });
});
/** ----- END OF WEBSOCKET ----- **/



function countOccupiedTables(orders) {
    const freeTables = document.querySelector('#free-tables');
    const occupiedTables = document.querySelector('#occupied-tables');

    let freeTablesCounter = 18 - orders.length;
    let occupiedTablesCounter = orders.length;

    freeTables.innerText = `Wolne: ${freeTablesCounter}`;
    occupiedTables.innerText = `Zajęte: ${occupiedTablesCounter}`;
}