const mainTableBody = document.querySelector('#orders-list-parent');

export function renderOrdersList(orders) {

    if (mainTableBody === null) {
        return;
    }

    const orderRows = document.querySelectorAll('.orders-list-table');
    orderRows.forEach(function (orderRow) {
        mainTableBody.removeChild(orderRow);
    });

    if(orders === null || orders.length === 0 || orders[0] === undefined) {

        const orderRow = document.createElement('div');
        orderRow.classList.add('orders-list-table');

        const orderNumberSpan = document.createElement('span');
        orderNumberSpan.classList.add('order-number');
        orderNumberSpan.innerText = 'Brak wyników';
        orderRow.appendChild(orderNumberSpan);
        mainTableBody.appendChild(orderRow);
        return;
    }

    orders.forEach(order => {

        const orderRow = document.createElement('div');
        orderRow.classList.add('orders-list-table');

        const idSpan = document.createElement('span');
        idSpan.classList.add('order-id');
        idSpan.innerText = order.id;

        const orderNumberSpan = document.createElement('span');
        orderNumberSpan.classList.add('order-number');
        orderNumberSpan.innerText = order.orderNumber;

        const dateTimeSpan = document.createElement('span');
        dateTimeSpan.classList.add('order-date-time');
        dateTimeSpan.innerText = order.orderTime;

        orderRow.appendChild(idSpan);
        orderRow.appendChild(orderNumberSpan);
        orderRow.appendChild(dateTimeSpan);
        mainTableBody.appendChild(orderRow);

    });
}