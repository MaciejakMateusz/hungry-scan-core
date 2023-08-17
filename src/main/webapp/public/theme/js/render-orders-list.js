const mainTableBody = document.querySelector('#rendered-body');

export function renderOrdersList(orders) {

    const orderRows = document.querySelectorAll('.order-rows');
    orderRows.forEach(function (orderRow) {
        mainTableBody.removeChild(orderRow);
    });

    orders.forEach(function (order) {
        //Creating necessary table elements
        const orderTr = document.createElement('tr');
        orderTr.classList.add('order-rows');

        //ID
        const orderIdTd = document.createElement('td');
        orderIdTd.innerText = order.id;

        //Order number
        const orderNumberTd = document.createElement('td');
        orderNumberTd.innerText = order.orderNumber;

        //Order time
        const orderTimeTd = document.createElement('td');
        orderTimeTd.innerText = order.orderTime;

        //Table number
        const restaurantTableTd = document.createElement('td');
        restaurantTableTd.innerText = order.restaurantTable.id;

        // ----- Nested table creation for orderedItems -----
        const orderedItemsTd = document.createElement('td');

        const nestedOrderedItemsTable = document.createElement('table');
        nestedOrderedItemsTable.classList.add('table');
        const nestedThead = document.createElement('thead');
        const nestedTr = document.createElement('tr');

        const nestedItemIdTh = document.createElement('th');
        nestedItemIdTh.innerText = "ID";
        const nestedItemNameTh = document.createElement('th');
        nestedItemNameTh.innerText = "Nazwa";
        const nestedItemPriceTh = document.createElement('th');
        nestedItemPriceTh.innerText = "Cena";
        const nestedItemQuantityTh = document.createElement('th');
        nestedItemQuantityTh.innerText = "Ilość";

        nestedTr.appendChild(nestedItemIdTh);
        nestedTr.appendChild(nestedItemNameTh);
        nestedTr.appendChild(nestedItemPriceTh);
        nestedTr.appendChild(nestedItemQuantityTh);
        nestedThead.appendChild(nestedTr);
        nestedOrderedItemsTable.appendChild(nestedThead);

        const nestedTbody = document.createElement('tbody');
        order.orderedItems.forEach(function (orderedItem) {
            const orderedItemTr = document.createElement('tr');

            const menuItemId = document.createElement('td');
            menuItemId.innerText = orderedItem.menuItem.id;
            const menuItemName = document.createElement('td');
            menuItemName.innerText = orderedItem.menuItem.name;
            const menuItemPrice = document.createElement('td');
            menuItemPrice.innerText = `${orderedItem.menuItem.price.toFixed(2)}zł`;
            const menuItemQuantity = document.createElement('td');
            menuItemQuantity.innerText = orderedItem.quantity;

            orderedItemTr.appendChild(menuItemId);
            orderedItemTr.appendChild(menuItemName);
            orderedItemTr.appendChild(menuItemPrice);
            orderedItemTr.appendChild(menuItemQuantity);

            nestedTbody.appendChild(orderedItemTr);

        });

        nestedOrderedItemsTable.appendChild(nestedTbody);
        orderedItemsTd.appendChild(nestedOrderedItemsTable);
        // End of nested table creation for ordered items

        //Total amount
        const totalAmountTd = document.createElement('td');
        totalAmountTd.innerText = `${order.totalAmount.toFixed(2)}zł`;

        //Is paid information  (yes/no)
        const paidTd = document.createElement('td');
        if (order.paid === true) {
            paidTd.innerText = 'Tak';
        } else {
            paidTd.innerText = 'Nie';
        }

        //Payment method
        const paymentMethodTd = document.createElement('td');
        if (order.paymentMethod === 'cash') {
            paymentMethodTd.innerText = 'Gotówka';
        } else if (order.paymentMethod === 'card') {
            paymentMethodTd.innerText = 'Karta';
        } else if (order.paymentMethod === 'online') {
            paymentMethodTd.innerText = 'Online';
        }

        //Connect all elements together
        orderTr.appendChild(orderIdTd)
        orderTr.appendChild(orderNumberTd)
        orderTr.appendChild(orderTimeTd)
        orderTr.appendChild(restaurantTableTd)
        orderTr.appendChild(orderedItemsTd)
        orderTr.appendChild(totalAmountTd)
        orderTr.appendChild(paidTd)
        orderTr.appendChild(paymentMethodTd)

        //Rendering action buttons for dine-in and take-away orders (to finalize order)
        if (order.forTakeAway === true) {
            const finishOrderTd = document.createElement('td');

            // Create the form element
            const form = document.createElement('form');
            form.action = '/restaurant/finalize-takeAway';
            form.method = 'POST';

            // Create the hidden input for 'id'
            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = `${order.id}`;
            form.appendChild(idInput);

            // Create the hidden input for 'isResolved'
            const isResolvedInput = document.createElement('input');
            isResolvedInput.type = 'hidden';
            isResolvedInput.name = 'isResolved';
            isResolvedInput.value = 'true';
            form.appendChild(isResolvedInput);

            //Crate the hidden input for 'paid'
            const paidInput = document.createElement('input');
            paidInput.type = 'hidden';
            paidInput.name = 'paid';
            paidInput.value = 'true';
            form.appendChild(paidInput);

            // Create the submit button
            const submitButton = document.createElement('button');
            submitButton.type = 'submit';
            submitButton.className = 'btn-primary';
            submitButton.textContent = 'Finalizuj';
            form.appendChild(submitButton);

            //Appending elements together
            finishOrderTd.appendChild(form);
            orderTr.appendChild(finishOrderTd);

        } else if (order.forTakeAway !== true && order.billRequested === true) {
            const finishOrderTd = document.createElement('td');

            // Create the form element
            const form = document.createElement('form');
            form.action = '/restaurant/finalize-dineIn';
            form.method = 'POST';

            // Create the hidden input for 'id'
            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = `${order.id}`;
            form.appendChild(idInput);

            // Create the hidden input for 'isResolved'
            const isResolvedInput = document.createElement('input');
            isResolvedInput.type = 'hidden';
            isResolvedInput.name = 'isResolved';
            isResolvedInput.value = 'true';
            form.appendChild(isResolvedInput);

            //Crate the hidden input for 'paid'
            const paidInput = document.createElement('input');
            paidInput.type = 'hidden';
            paidInput.name = 'paid';
            paidInput.value = 'true';
            form.appendChild(paidInput);

            // Create the submit button
            const submitButton = document.createElement('button');
            submitButton.type = 'submit';
            submitButton.className = 'btn-primary';
            submitButton.textContent = 'Finalizuj';
            form.appendChild(submitButton);

            //Appending elements together
            finishOrderTd.appendChild(form);
            orderTr.appendChild(finishOrderTd);
        }

        //Appending order row to static mainTableBody
        mainTableBody.appendChild(orderTr);
    });

}