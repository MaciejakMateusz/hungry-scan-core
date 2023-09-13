/** ----- DATE & TIME UPDATES ----- **/
export function updateDateTime() {
    const currentDate = new Date();
    const dateElement = document.querySelector('#date')
    const timeElement = document.querySelector('#time')

    const options = {year: 'numeric', month: 'long', day: 'numeric'};
    const formattedDate = currentDate.toLocaleDateString('pl-PL', options);

    const timeOptions = {hour: '2-digit', minute: '2-digit'};
    const formattedTime = currentDate.toLocaleTimeString('pl-PL', timeOptions);

    dateElement.innerText = formattedDate;
    timeElement.innerText = formattedTime;
}

setInterval(updateDateTime, 1000);

/** ----- END OF DATE & TIME UPDATES ----- **/


/** ----- FETCH FUNCTIONS ----- **/
const apiHost = 'http://localhost:8082/api'

export function fetchOrders() {
    return fetch(`${apiHost}/orders`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders");
            }
        }).then(data => {
            return data;
        }).catch(error => {
            console.log(error);
        });
}

export function fetchOrderByTableNumber(id) {
    return fetch(`${apiHost}/orders/${id}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders");
            }
        }).then(data => {
            return data;
        }).catch(error => {
            console.log(error);
        });
}

export function fetchFinalizedOrders(forTakeAway, limit, offset) {
    const requestBody = JSON.stringify({
        forTakeAway: forTakeAway,
        limit: limit,
        offset: offset
    });

    return fetch(`${apiHost}/orders/finalized`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: requestBody
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Communication error: POST /api/orders/finalized');
        }
    }).then(data => {
        return data;
    }).catch(error => {
        console.log(error);
    });
}

export function fetchAllResolved() {
    return fetch(`${apiHost}/orders/resolved`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/resolved");
            }
        }).then(data => {
            return data;
        }).catch(error => {
            console.log(error);
        });
}

export function fetchOrderById(id) {
    return fetch(`${apiHost}/orders/id/${id}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/id");
            }
        }).then(data => {
            return data;
        }).catch(error => {
            console.log(error);
        });
}

export function fetchTakeAwayOrders() {
    return fetch(`${apiHost}/orders/takeAway`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/takeAway");
            }
        }).then(data => {
            return data;
        }).catch(error => {
            console.log(error);
        });
}

export function fetchDineInOrders() {
    return fetch(`${apiHost}/orders`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders");
            }
        }).then(data => {
            return data;
        }).catch(error => {
            console.log(error);
        });
}

export function fetchFinalizedOrderById(id, forTakeAway) {
    return fetch(`${apiHost}/orders/finalized/id/${id}/${forTakeAway}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/finalized");
            }
        }).then(data => {
            return data;
        }).catch(error => {
            console.log(error);
        });
}

export function fetchFinalizedOrdersByDate(date, forTakeAway) {
    return fetch(`${apiHost}/orders/finalized/date/${date}/${forTakeAway}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/finalized");
            }
        }).then(data => {
            return data;
        }).catch(error => {
            console.log(error);
        });
}

/** ----- END OF FETCH FUNCTIONS ----- **/