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
export function fetchOrders() {
    return fetch(`http://localhost:8082/api/orders`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

export function fetchOrderByTableNumber(id) {
    return fetch(`http://localhost:8082/api/orders/${id}`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

export function fetchFinalizedOrders(forTakeAway, limit, offset) {
    const requestBody = JSON.stringify({
        forTakeAway: forTakeAway,
        limit: limit,
        offset: offset
    });

    return fetch('http://localhost:8082/api/orders/finalized', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: requestBody
    }).then(function (response) {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Communication error: POST /api/orders/finalized');
        }
    }).then(function (data) {
        return data;
    }).catch(function (error) {
        console.log(error);
    });
}

export function fetchAllResolved() {
    return fetch(`http://localhost:8082/api/orders/resolved`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/resolved");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

export function fetchOrderById(id) {
    return fetch(`http://localhost:8082/api/orders/id/${id}`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/id");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

export function fetchTakeAwayOrders() {
    return fetch(`http://localhost:8082/api/orders/takeAway`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/takeAway");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

export function fetchDineInOrders() {
    return fetch(`http://localhost:8082/api/orders`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

export function fetchAllResolvedTakeAway() {
    return fetch(`http://localhost:8082/api/orders/resolved/take-away`)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("Communication error: GET /api/orders/resolved");
            }
        }).then(function (data) {
            return data;
        }).catch(function (error) {
            console.log(error);
        });
}

/** ----- END OF FETCH FUNCTIONS ----- **/