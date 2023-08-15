const orderContainer = document.querySelector('#order-container')

document.addEventListener("DOMContentLoaded", function () {
    fetchExistingOrders().then(function (orders) {
        renderOrders(orders)
    });
});

function fetchExistingOrders() {
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
        orderIdH3.innerText = `Numer zamówienia: ${order.orderNumber}`;
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

        const orderPaymentMethod = document.createElement('p');
        orderPaymentMethod.innerText = `Metoda płatności: ${order.paymentMethod}`;
        orderCard.appendChild(orderPaymentMethod);

        const orderTableNumber = document.createElement('p');
        orderTableNumber.innerText = `Numer stolika: ${order.restaurantTable.id}`;
        orderCard.appendChild(orderTableNumber);

        const orderTotalAmount = document.createElement('p');
        orderTotalAmount.innerText = `Do zapłaty: ${order.totalAmount}`;
        orderCard.appendChild(orderTotalAmount);

        // Create button to finish order when bill is requested from customer
        if (order.billRequested === true) {

            // Create the form element
            const form = document.createElement('form');
            form.action = '/restaurant';
            form.method = 'POST';

            // Create the hidden input for 'id'
            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = `${order.id}`;
            form.appendChild(idInput);

            // Create the hidden input for 'paid'
            const paidInput = document.createElement('input');
            paidInput.type = 'hidden';
            paidInput.name = 'paid';
            paidInput.value = 'true';
            form.appendChild(paidInput);

            // Create the submit button
            const submitButton = document.createElement('button');
            submitButton.type = 'submit';
            submitButton.className = 'btn-primary';
            submitButton.textContent = 'Klient opłacił zamówienie';
            form.appendChild(submitButton);
            // Preventing page reloading
            form.addEventListener('submit', function (event) {
                event.preventDefault();

                // Create an object to hold form data
                const formData = new FormData(form);

                // Perform an AJAX request to send the form data
                fetch('/restaurant', {
                    method: 'POST',
                    body: formData
                }).then(function (response) {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error("Error while submitting order data to /restaurant URL");
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            });

            orderCard.appendChild(form);
        }

        const orderBody = document.createElement('div');
        orderBody.appendChild(orderCard);
        orderBody.classList.add('order-body');
        orderContainer.appendChild(orderBody);
    });
}