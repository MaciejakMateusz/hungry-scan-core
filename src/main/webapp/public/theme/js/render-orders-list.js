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

        const orderIdTd = document.createElement('td');
        orderIdTd.innerText = order.id;
        const orderNumberTd = document.createElement('td');
        orderNumberTd.innerText = order.orderNumber;
        const orderTimeTd = document.createElement('td');
        orderTimeTd.innerText = order.orderTime;

        //Nested table creation
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

        nestedTr.appendChild(nestedItemIdTh);
        nestedTr.appendChild(nestedItemNameTh);
        nestedTr.appendChild(nestedItemPriceTh);
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

            orderedItemTr.appendChild(menuItemId);
            orderedItemTr.appendChild(menuItemName);
            orderedItemTr.appendChild(menuItemPrice);

            nestedTbody.appendChild(orderedItemTr);

        });

        nestedOrderedItemsTable.appendChild(nestedTbody);
        orderedItemsTd.appendChild(nestedOrderedItemsTable);

        const paidTd = document.createElement('td');
        if(order.paid === true) {
            paidTd.innerText = 'Tak';
        } else {
            paidTd.innerText = 'Nie';
        }
        const paymentMethodTd = document.createElement('td');
        paymentMethodTd.innerText = order.paymentMethod;
        const restaurantTableTd = document.createElement('td');
        restaurantTableTd.innerText = order.restaurantTable.id;
        const totalAmountTd = document.createElement('td');
        totalAmountTd.innerText = `${order.totalAmount.toFixed(2)}zł`;

        //Connect all elements together
        orderTr.appendChild(orderIdTd)
        orderTr.appendChild(orderNumberTd)
        orderTr.appendChild(orderTimeTd)
        orderTr.appendChild(orderedItemsTd)
        orderTr.appendChild(paidTd)
        orderTr.appendChild(paymentMethodTd)
        orderTr.appendChild(restaurantTableTd)
        orderTr.appendChild(totalAmountTd)

        mainTableBody.appendChild(orderTr);
    });

}