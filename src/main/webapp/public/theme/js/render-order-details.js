const tableNumber = document.querySelector('#table-number');
const orderTime = document.querySelector('#order-time');
const orderDetails = document.querySelector('#order-details');
const paymentMethod = document.querySelector('#p-method');
const totalPriceSpans = document.querySelectorAll('.total-price-span');
const orderedTotalAmountSection = document.querySelector('#ordered-total-amount-section');
const billRequestedSection = document.querySelector('#bill-requested-finalize-section');        // Selecting the finalize form
const finalizeButton = document.querySelector('#finalize-button');



/** ----- RENDERING ORDER DETAILS ON THE RIGHT PANEL ----- **/
export function renderOrderDetails(order) {

    if(!order.forTakeAway) {
        tableNumber.innerText = `Stolik ${order.restaurantTable.id}`;
    }
    orderTime.innerText = `Godzina zamówienia: ${order.orderTime.substring(0, 5)}`;

    let sum = 0;
    order.orderedItems.forEach(orderedItem => {
        const orderedItemsGroup = document.createElement('div');
        orderedItemsGroup.classList.add('ordered-items-group');

        let orderedItemInfo = document.createElement('span');
        orderedItemInfo.classList.add('item-info');
        orderedItemInfo.innerText = `${orderedItem.quantity}x ${orderedItem.menuItem.name}`;
        orderedItemsGroup.appendChild(orderedItemInfo);

        let itemPriceInfo = document.createElement('span');
        itemPriceInfo.classList.add('item-price');
        itemPriceInfo.innerText = `${orderedItem.menuItem.price * orderedItem.quantity} zł`;
        orderedItemsGroup.appendChild(itemPriceInfo);

        orderDetails.appendChild(orderedItemsGroup);

        sum += orderedItem.menuItem.price * orderedItem.quantity;
    });

    if (order.paymentMethod === 'cash') {
        paymentMethod.innerText = 'Gotówką';
    } else if (order.paymentMethod === 'card') {
        paymentMethod.innerText = 'Kartą';
    } else if (order.paymentMethod === 'online') {
        paymentMethod.innerText = 'Online';
    }

    totalPriceSpans.forEach(totalPrice => {
        totalPrice.innerText = `${sum} zł`
    });

    const orderDetailsSection = document.querySelector('#order-details-section');
    if (order.billRequested || order.forTakeAway) {

        if(!order.resolved) {
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


/** ----- CLEAR THE RIGHT PANEL TO REPOPULATE IT ----- **/
export function clearOrderDetails() {
    orderTime.innerText = '';
    orderDetails.innerHTML = ''; // Remove all child elements

    const orderedItemsGroups = document.querySelectorAll('.ordered-items-group');
    orderedItemsGroups.forEach(group => {
        group.remove();
    });

    totalPriceSpans.forEach(totalPrice => {
        totalPrice.innerText = '0 zł'; // Reset total prices
    });

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