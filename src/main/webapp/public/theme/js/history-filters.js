import {fetchFinalizedOrderById, fetchFinalizedOrdersByDate} from "./utils.js";
import {renderOrdersList} from "./render-orders-list.js";

const idFilterForm = document.querySelector('#id-filter');
const dateFilterForm = document.querySelector('#date-filter');
const ordersListParent = document.querySelector('#orders-list-parent');
const ordersListBox = document.querySelector('.orders-list-box');

export function findOrderByIdAndType(isForTakeAway) {

    const idField = idFilterForm.querySelector('#search-id');
    let idValue = idField.value;

    if (idValue !== null && idValue !== "") {
        clearHistoryList();

        fetchFinalizedOrderById(idValue, isForTakeAway).then(order => {
            let collection = [order];
            if (collection[0] !== undefined) {
                renderOrdersList(collection);
            }
        });
    }
}

export function findOrdersByDate(isForTakeAway) {

    const dateField = dateFilterForm.querySelector('#search-date');
    let dateValue = dateField.value;

    let formattedDate = formatDate(dateValue);

    if (dateValue !== null && dateValue !== "") {
        clearHistoryList();

        fetchFinalizedOrdersByDate(formattedDate, isForTakeAway).then(orders => {
            renderOrdersList(orders);
        });
    }
}


export function clearHistoryList() {

    const orderRows = Array.from(ordersListParent.children);

    if (orderRows !== null) {
        orderRows.forEach(orderRow => {
            orderRow.remove();
        });
    }

    const paginationButtonsDiv = ordersListBox.querySelector('.pagination-buttons');
    if (paginationButtonsDiv !== null) {
        paginationButtonsDiv.remove();
    }
}

function formatDate(inputDate) {
    const jsDate = new Date(inputDate);
    const year = jsDate.getFullYear();
    const month = (jsDate.getMonth() + 1).toString().padStart(2, '0'); // Month is zero-indexed
    const day = jsDate.getDate().toString().padStart(2, '0');

    return `${day}-${month}-${year}`;
}