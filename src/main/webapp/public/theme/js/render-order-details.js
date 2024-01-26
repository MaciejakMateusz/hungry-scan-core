const tableNumber = document.querySelector('#table-number');
const orderTime = document.querySelector('#order-time');
const orderId = document.querySelector('#order-id');
const orderDetails = document.querySelector('#order-details');
const paymentMethod = document.querySelector('#p-method');
const totalPriceSpans = document.querySelectorAll('.total-price-span');
const orderedTotalAmountSection = document.querySelector('#ordered-total-amount-section');
const billRequestedSection = document.querySelector('#bill-requested-finalize-section');
const finalizeButton = document.querySelector('#finalize-button');
const resolveCallForm = document.querySelector('#resolve-call-form');

/** ----- WEBSOCKET ----- **/
const socket = new WebSocket('ws://localhost:8082/order-websocket');
const stompClient = Stomp.over(socket);

stompClient.connect({},  () => {
    stompClient.subscribe('/topic/restaurant-order', message => {
        const order = JSON.parse(message.body);
        renderOrderDetails(order);
    });
});

/** ----- END OF WEBSOCKET ----- **/

/** ----- RENDERING ORDER DETAILS ON THE RIGHT PANEL ----- **/
export function renderOrderDetails(order) {

    clearOrderDetails();

    if(order === null) {
        return
    }

    if (!order.forTakeAway) {
        tableNumber.innerText = `Stolik ${order.restaurantTable.id}`;
    }
    orderId.innerText = `ID zamówienia: ${order.id}`;
    orderTime.innerText = `Godzina zamówienia: ${order.orderTime.substring(0, 5)}`;

    let sum = 0;
    order.orderedItems.forEach(orderedItem => {
        const orderedItemsGroup = document.createElement('div');
        orderedItemsGroup.classList.add('ordered-items-group');

        let orderedItemQuantity = document.createElement('span');
        orderedItemQuantity.classList.add('item-quantity');
        orderedItemQuantity.innerText = `${orderedItem.quantity}x`;
        orderedItemsGroup.appendChild(orderedItemQuantity);

        let orderedItemInfo = document.createElement('span');
        orderedItemInfo.classList.add('item-info');
        orderedItemInfo.innerText = `${orderedItem.menuItem.name}`;
        orderedItemsGroup.appendChild(orderedItemInfo);

        let itemPrice = parseFloat(orderedItem.menuItem.price);
        let itemQuantity = parseInt(orderedItem.quantity);

        let itemPriceInfo = document.createElement('span');
        itemPriceInfo.classList.add('item-price');
        itemPriceInfo.innerText = `${(itemPrice * itemQuantity)} zł`;
        orderedItemsGroup.appendChild(itemPriceInfo);

        orderDetails.appendChild(orderedItemsGroup);

        sum += itemPrice * itemQuantity;
    });

    if (order.paymentMethod === 'cash') {
        paymentMethod.innerText = 'Gotówką';
    } else if (order.paymentMethod === 'card') {
        paymentMethod.innerText = 'Kartą';
    } else if (order.paymentMethod === 'online') {
        paymentMethod.innerText = 'Online';
    }

    totalPriceSpans.forEach(totalPrice =>
        totalPrice.innerText = `${sum.toFixed(2)} zł`);

    if (order.waiterCalled) {
        const rightBottomInfoWrapper = document.querySelector('#right-bottom-info-wrapper');
        const waiterCallWrapper = document.querySelector('#waiter-call-wrapper');

        rightBottomInfoWrapper.classList.add('d-none');
        waiterCallWrapper.classList.remove('d-none');

        let idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'id';
        idInput.value = `${order.id}`;
        resolveCallForm.appendChild(idInput);

        resolveCallForm.addEventListener('click', e => {
            e.preventDefault();
            resolveCallForm.submit();
        });
    }

    const orderDetailsSection = document.querySelector('#order-details-section');
    if (order.billRequested || order.forTakeAway) {

        if(order.forTakeAway) {
            tableNumber.innerText = 'Na wynos';
        }

        if (!order.resolved) {
            orderDetailsSection.classList.remove('no-button');
            orderedTotalAmountSection.classList.add('d-none');
            billRequestedSection.classList.remove('d-none');

            // Create the hidden input for 'id'
            let idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = `${order.id}`;
            finalizeButton.appendChild(idInput);

            // Create the hidden input for 'isResolved'
            let isResolvedInput = document.createElement('input');
            isResolvedInput.type = 'hidden';
            isResolvedInput.name = 'isResolved';
            isResolvedInput.value = 'true';
            finalizeButton.appendChild(isResolvedInput);

            //Crate the hidden input for 'paid'
            let paidInput = document.createElement('input');
            paidInput.type = 'hidden';
            paidInput.name = 'paid';
            paidInput.value = 'true';
            finalizeButton.appendChild(paidInput);

            finalizeButton.addEventListener('click', e => {
                e.preventDefault();
                finalizeButton.submit();
            });
        } else {
            orderDetailsSection.classList.add('no-button');
            orderedTotalAmountSection.classList.remove('d-none');
            billRequestedSection.classList.add('d-none');
        }
    } else {
        orderDetailsSection.classList.add('no-button');
        orderedTotalAmountSection.classList.remove('d-none');
        billRequestedSection.classList.add('d-none');
    }
}

/** ----- END OF RENDERING ORDER DETAILS ON THE RIGHT PANEL ----- **/


/** ----- CLEARING THE RIGHT PANEL----- **/
export function clearOrderDetails() {
    tableNumber.innerText = '';
    orderId.innerText = '';
    orderTime.innerText = '';
    orderDetails.innerHTML = ''; // Remove all child elements

    const orderedItemsGroups = document.querySelectorAll('.ordered-items-group');
    orderedItemsGroups.forEach(group =>
        group.remove());

    totalPriceSpans.forEach(totalPrice =>
        totalPrice.innerText = '0 zł'); // Reset total prices

    const finalizeFormChildren = Array.from(finalizeButton.children);
    finalizeFormChildren.forEach(child => {
        if (child.tagName !== 'SPAN') {
            child.remove();
        }
    });

    orderedTotalAmountSection.classList.remove('d-none');
    billRequestedSection.classList.add('d-none');
}
/** ----- END OF CLEARING THE RIGHT PANEL ----- **/