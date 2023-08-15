const orderContainer = document.querySelector('#order-container')


const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/restaurant-order', function (message) {
        const orders = JSON.parse(message.body);
        console.log('Received order: ', orders);
        renderOrders(orders);
    });
});

function renderOrders(orders) {

    const existingOrderBodies = document.querySelectorAll('.order-body');
    existingOrderBodies.forEach(function (existingOrderBody) {
        orderContainer.removeChild(existingOrderBody);
    });

    orders.forEach(function (order) {
        const orderCard = document.createElement('div');
        orderCard.classList.add('card', 'shadow', 'bg-white', 'order');

        const orderIdH3 = document.createElement('h3');
        orderIdH3.innerText = `Identyfikator zamówienia: ${order.id}`;
        orderCard.appendChild(orderIdH3);

        const orderedItemsCard = document.createElement('div');
        orderedItemsCard.classList.add('card', 'shadow');

        const orderedItemsParagraph = document.createElement('p');
        orderedItemsParagraph.innerText = 'Zamówione pozycje:';
        orderedItemsCard.appendChild(orderedItemsParagraph);

        order.orderedItems.forEach(function (orderedItem) {
            const orderedItemId = document.createElement('p');
            orderedItemId.innerText = `ID: ${orderedItem.menuItem.id}`;
            const orderedItemName = document.createElement('p');
            orderedItemName.innerText = `Nazwa: ${orderedItem.menuItem.name}`;
            const orderedItemPrice = document.createElement('p');
            orderedItemPrice.innerText = `Cena: ${orderedItem.menuItem.price}`;
            orderedItemsCard.appendChild(orderedItemId);
            orderedItemsCard.appendChild(orderedItemName);
            orderedItemsCard.appendChild(orderedItemPrice);
        });
        orderCard.appendChild(orderedItemsCard);

        const orderTime = document.createElement('p');
        orderTime.innerText = `Czas zamówienia: ${order.orderTime}`;
        orderCard.appendChild(orderTime);

        const orderPaidInfo = document.createElement('p');
        orderPaidInfo.innerText = `Zapłacono: ${order.paid}`;
        orderCard.appendChild(orderPaidInfo);

        const orderPaymentMethod = document.createElement('p');
        orderPaymentMethod.innerText = `Metoda płatności: ${order.paymentMethod}`;
        orderCard.appendChild(orderPaymentMethod);

        const orderTableNumber = document.createElement('p');
        orderTableNumber.innerText = `Numer stolika: ${order.restaurantTable.id}`;
        orderCard.appendChild(orderTableNumber);

        const orderTotalAmount = document.createElement('p');
        orderTotalAmount.innerText = `Do zapłaty: ${order.totalAmount}`;
        orderCard.appendChild(orderTotalAmount);

        const orderBody = document.createElement('div');
        orderBody.appendChild(orderCard);
        orderBody.classList.add('order-body');
        orderContainer.appendChild(orderBody);
    });
}