import {fetchAllResolved, fetchFinalizedOrders, updateDateTime} from "./utils.js";
import {clearOrderDetails, renderOrderDetails} from "./render-order-details.js";
import {renderOrdersList} from "./render-orders-list.js";

const ordersListParent = document.querySelector('#orders-list-parent');

export function renderRecordsPerPage(isForTakeAway) {
    updateDateTime();

    let forTakeAway = isForTakeAway;
    const recordsPerPage = 20;

    let selectedPaginationButton = document.querySelector('.pagination-button-selected');
    let pageNumber = parseInt(selectedPaginationButton.firstElementChild.innerText);
    let offset = (pageNumber - 1) * 20;

    fetchFinalizedOrders(forTakeAway, recordsPerPage, offset).then(orders => {

        clearOrderDetails();
        renderOrdersList(orders);
        renderOrderDetails(orders[0])

        ordersListParent.firstElementChild.classList.add('selected-list-element');
    });
}

export function renderPaginationButtons(isForTakeAway) {
    const recordsPerPage = 20;

    fetchAllResolved().then(orders => {

        let totalOrders = 0;
        orders.forEach((order) => {
            if (order.forTakeAway === isForTakeAway) {
                totalOrders++;
            }
        });

        const numberOfPages = Math.ceil(totalOrders / recordsPerPage);

        const paginationButtonsDiv = document.querySelector('.pagination-buttons');

        for (let i = 2; i <= numberOfPages; i++) {
            let paginationButton = document.createElement('div');
            paginationButton.classList.add('pagination-button');
            let pageNumber = document.createElement('span');
            pageNumber.innerText = i.toString();
            paginationButton.appendChild(pageNumber);

            paginationButtonsDiv.appendChild(paginationButton);

            const pageNumberButtons = document.querySelectorAll('.pagination-button');

            if (pageNumberButtons !== null) {
                pageNumberButtons.forEach(pageButton => {
                    pageButton.addEventListener('click', () => {

                        let selectedPaginationButton = document.querySelector('.pagination-button-selected');
                        selectedPaginationButton.classList.remove('pagination-button-selected');

                        pageButton.classList.add('pagination-button-selected');
                        renderRecordsPerPage(isForTakeAway);
                    });
                });
            }
        }
    });
}