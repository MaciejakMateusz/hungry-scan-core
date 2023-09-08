import {fetchOrderById} from "./utils.js";
import {clearOrderDetails, renderOrderDetails} from "./render-order-details.js";
import {renderPaginationButtons, renderRecordsPerPage} from "./history-pagination.js";
import {findOrderByIdAndType, findOrdersByDate} from "./history-filters.js";

const ordersListParent = document.querySelector('#orders-list-parent');
const idFilterForm = document.querySelector('#id-filter');
const idFilterButton = document.querySelector('#id-filter-button');
const dateFilterForm = document.querySelector('#date-filter');
const dateFilterButton = document.querySelector('#date-filter-button');
const idSearchTrigger = document.querySelector('#trigger-id-filter');
const dateSearchTrigger = document.querySelector('#trigger-date-filter');

document.addEventListener("DOMContentLoaded", function () {
    renderPaginationButtons(false);
    renderRecordsPerPage(false);
});

idSearchTrigger.addEventListener('click', e => {
    e.preventDefault();
    idFilterButton.click();
});

idFilterForm.addEventListener('submit', e => {
    e.preventDefault();
    findOrderByIdAndType(false);
});

dateSearchTrigger.addEventListener('click', e => {
    e.preventDefault();
    dateFilterButton.click();
});

dateFilterForm.addEventListener('submit', e => {
    e.preventDefault();
    findOrdersByDate(false);
});

const observer = new MutationObserver(function (mutationsList) {
    for (const mutation of mutationsList) {
        if (mutation.type === 'childList') {
            mutation.addedNodes.forEach(function (addedNode) {
                if (addedNode.nodeType === Node.ELEMENT_NODE && addedNode.classList.contains('orders-list-table')) {
                    addedNode.addEventListener('click', () => {

                        if(addedNode.firstElementChild.innerText === 'Brak wyników') {
                            return;
                        }

                        //Remove the class from all elements
                        const orderListTables = document.querySelectorAll('.orders-list-table');
                        orderListTables.forEach(element => {
                            element.classList.remove('selected-list-element');
                        });

                        //Add the class to the clicked element
                        addedNode.classList.add('selected-list-element');

                        let orderId = addedNode.firstElementChild.innerText;
                        fetchOrderById(orderId).then(order => {
                            clearOrderDetails();
                            renderOrderDetails(order);
                        });
                    });
                }
            });
        }
    }
});

observer.observe(ordersListParent, {childList: true});
