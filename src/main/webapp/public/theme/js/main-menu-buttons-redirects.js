/** ----- MAIN MENU BUTTONS REDIRECTS ----- **/
const menuButtons = document.querySelectorAll('.main-button');

const mainViewButton = document.querySelector('#main-view-button');
const takeAwayButton = document.querySelector('#take-away-button');
// const reservationsButton = document.querySelector('#reservations-button');
const restaurantMenuButton = document.querySelector('#menu-button');
const finalizedOrdersButton = document.querySelector('#finalized-orders-button');
const logoutButton = document.querySelector('#logout-button');
const cmsButton = document.querySelector('#cms-button');
const finalizedDineInButton = document.querySelector('#finalized-dine-in-button');
const finalizedTakeAwayButton = document.querySelector('#finalized-take-away-button');
const addItemButton = document.querySelector('#add-item-button');
const backButton = document.querySelector('#back-button');
const cmsItemsManagementButton = document.querySelector('#menu-items-management');
const cmsCategoryManagementButton = document.querySelector('#categories-management');
const addCategoryButton = document.querySelector('#add-category-button');
const cancelItemFormButton = document.querySelector('#cancel-item-action-button');
const cancelCategoryFormButton = document.querySelector('#cancel-category-action-button');


document.addEventListener("DOMContentLoaded", () => {

    menuButtons.forEach(function (menuButton) {
        if (!menuButton.classList.contains('inactive-main-button')) {
            menuButton.addEventListener('click', function (e) {
                e.preventDefault()

                document.querySelector('.selected-button')
                    .classList
                    .remove('selected-button');

                e.currentTarget.classList.add('selected-button');
            });
        }
    });

    if (mainViewButton !== null) {
        mainViewButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant";
        });
    }

    if (takeAwayButton !== null) {
        takeAwayButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/orders/take-away";
        });
    }

    // if (reservationsButton !== null) {
    //     reservationsButton.addEventListener('click', function () {
    //         // window.location.href = "http://localhost:8082/restaurant/bookings";
    //     });
    // }

    if (restaurantMenuButton !== null) {
        restaurantMenuButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/menu";
        });
    }

    if (finalizedOrdersButton !== null) {
        finalizedOrdersButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/orders/finalized";
        });
    }

    if (logoutButton !== null) {
        logoutButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/logout";
        });
    }

    if (cmsButton !== null) {
        cmsButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/cms/items";
        });
    }

    if (finalizedDineInButton !== null) {
        finalizedDineInButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/orders/finalized";
        });
    }

    if (finalizedTakeAwayButton !== null) {
        finalizedTakeAwayButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/orders/finalized/take-away";
        });
    }

    if (addItemButton !== null) {
        addItemButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/cms/items/add";
        });
    }

    if (backButton !== null) {
        backButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant";
        });
    }

    if (cmsItemsManagementButton !== null) {
        cmsItemsManagementButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/cms/items";
        });
    }

    if (cmsCategoryManagementButton !== null) {
        cmsCategoryManagementButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/cms/categories";
        });
    }

    if (addCategoryButton !== null) {
        addCategoryButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/cms/categories/add";
        });
    }

    if (cancelItemFormButton !== null) {
        cancelItemFormButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/cms/items";
        });
    }

    if (cancelCategoryFormButton !== null) {
        cancelCategoryFormButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/cms/categories";
        });
    }

});
/** ----- END OF MAIN MENU BUTTONS REDIRECTS ----- **/