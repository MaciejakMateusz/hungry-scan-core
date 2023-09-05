import {updateDateTime} from "./utils.js";

/** ---- ON PAGE LOAD ----- **/
document.addEventListener('DOMContentLoaded', function () {
    updateDateTime();
    indicateCheckedRadio();
});
/** ---- END OF ON PAGE LOAD ----- **/

const customRadios = document.querySelectorAll('.custom-radio');

/** ---- RADIO BUTTONS HANDLER ----- **/
customRadios.forEach((radio) => {
    radio.addEventListener('click', () => {
        radio.checked = true;
        indicateCheckedRadio();
    });
});

const availableLabel = document.querySelector('#available-label');
const unavailableLabel = document.querySelector('#unavailable-label');

function indicateCheckedRadio() {

    if (availableLabel !== null || unavailableLabel !== null) {
        const availableRadio = availableLabel.firstElementChild;
        const unavailableRadio = unavailableLabel.firstElementChild;

        if (availableRadio.checked) {
            if (!availableLabel.querySelector('.check-icon')) {
                const checkIcon = document.createElement('span');
                checkIcon.classList.add('check-icon');
                checkIcon.classList.add('available-icon-position');
                availableLabel.appendChild(checkIcon);
            }
        } else {
            const currentCheckedIndicator = availableLabel.querySelector('.check-icon');
            if (currentCheckedIndicator) {
                currentCheckedIndicator.remove();
            }
        }

        if (unavailableRadio.checked) {
            if (!unavailableLabel.querySelector('.check-icon')) {
                const xIcon = document.createElement('span');
                xIcon.classList.add('x-icon');
                xIcon.classList.add('unavailable-icon-position');
                unavailableLabel.appendChild(xIcon);
            }
        } else {
            const currentCheckedIndicator = unavailableLabel.querySelector('.x-icon');
            if (currentCheckedIndicator) {
                currentCheckedIndicator.remove();
            }
        }
    }
}

/** ---- END OF RADIO BUTTONS HANDLER ----- **/
