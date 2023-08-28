/** ----- MAIN MENU BUTTONS REDIRECTS ----- **/
const menuButtons = document.querySelectorAll('.main-button');

const mainViewButton = document.querySelector('#main-view-button');
const takeAwayButton = document.querySelector('#take-away-button');
const reservationsButton = document.querySelector('#reservations-button');
const restaurantMenuButton = document.querySelector('#menu-button');
const finalizedOrdersButton = document.querySelector('#finalized-orders-button');
const logoutButton = document.querySelector('#logout-button');
const cmsButton = document.querySelector('#cms-button');
const finalizedDineInButton = document.querySelector('#finalized-dine-in-button');
const finalizedTakeAwayButton = document.querySelector('#finalized-take-away-button');
document.addEventListener("DOMContentLoaded", () => {

    menuButtons.forEach(function (menuButton) {
        if(!menuButton.classList.contains('inactive-menu-button')) {
            menuButton.addEventListener('click', function (e) {
                e.preventDefault()

                document.querySelector('.selected-button')
                    .classList
                    .remove('selected-button');

                e.currentTarget.classList.add('selected-button');
            });
        }
    });

    mainViewButton.addEventListener('click', function () {
        window.location.href = "http://localhost:8082/restaurant";
    });

    takeAwayButton.addEventListener('click', function () {
        window.location.href = "http://localhost:8082/restaurant/orders/take-away";
    });

    reservationsButton.addEventListener('click', function () {
        // window.location.href = "http://localhost:8082/restaurant/bookings";
    });

    restaurantMenuButton.addEventListener('click', function () {
        window.location.href = "http://localhost:8082/restaurant/menu";
    });

    finalizedOrdersButton.addEventListener('click', function () {
        window.location.href = "http://localhost:8082/restaurant/orders/finalized";
    });

    logoutButton.addEventListener('click', function () {
        window.location.href = "http://localhost:8082/restaurant/logout";
    });

    if(cmsButton!== null) {
        cmsButton.addEventListener('click', function () {
            window.location.href = "http://localhost:8082/restaurant/cms";
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

});
/** ----- END OF MAIN MENU BUTTONS REDIRECTS ----- **/