/** ----- WEBSOCKET ----- **/
const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function () {
    stompClient.subscribe('/topic/waiter-call', function (message) {
        const waiterCall = JSON.parse(message.body);
        // Coś zrobić z wezwaniem klenera
    });
});
/** ----- END OF WEBSOCKET ----- **/


function renderWaiterCall(waiterCall) {
    const rightColInfoContainer = document.querySelector('.right-column-info-container');
    const infoContainerChildren = Array.from(rightColInfoContainer.children);

    infoContainerChildren.forEach(child => {
        child.remove();
    });

}