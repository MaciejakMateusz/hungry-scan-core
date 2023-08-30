import {fetchOrderById} from "./utils.js";
import {clearOrderDetails, renderOrderDetails} from "./render-order-details.js";
import {renderPaginationButtons, renderRecordsPerPage} from "./history-pagination.js";

const ordersListParent = document.querySelector('#orders-list-parent');

document.addEventListener("DOMContentLoaded", function () {
    renderPaginationButtons(true);
    renderRecordsPerPage(true);
});

const observer = new MutationObserver(function (mutationsList) {
    for (const mutation of mutationsList) {
        if (mutation.type === 'childList') {
            mutation.addedNodes.forEach(function (addedNode) {
                if (addedNode.nodeType === Node.ELEMENT_NODE && addedNode.classList.contains('orders-list-table')) {
                    addedNode.addEventListener('click', () => {

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
